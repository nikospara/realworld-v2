package realworld.keycloak.listener;

import static org.keycloak.events.EventType.*;
import static org.keycloak.events.admin.ResourceType.USER;
import static realworld.user.model.events.UserModificationEventType.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import realworld.user.model.UserUpdateData;
import realworld.user.model.events.UserModificationEvent;

/**
 * The Keycloak event handler that actually forwards the event to Kafka.
 *
 * @see RealworldEventListenerProviderFactory The RealworldEventListenerProviderFactory for the configuration logic.
 */
class RealworldEventListenerProvider implements EventListenerProvider {

	private static final Logger LOG = Logger.getLogger(RealworldEventListenerProvider.class.getName());

	private static final EnumSet<EventType> INTERESTING_EVENT_TYPES = EnumSet.of(REGISTER, UPDATE_EMAIL, UPDATE_PROFILE);
	private static final EnumSet<OperationType> INTERESTING_ADMIN_OP_TYPES = EnumSet.of(OperationType.CREATE, OperationType.DELETE, OperationType.UPDATE);

	private KeycloakSession session;
	private Producer<String, String> producer;
	private ObjectMapper om;
	private String topicName;

	RealworldEventListenerProvider(KeycloakSession session, Producer<String, String> producer, ObjectMapper om, String topicName) {
		this.session = session;
		this.producer = producer;
		this.om = om;
		this.topicName = topicName;
	}

	@Override
	public void onEvent(Event event) {
		if( INTERESTING_EVENT_TYPES.contains(event.getType()) ) {
			boolean success = false;
			try {
				UserModel user = session.users().getUserById(event.getUserId(), session.realms().getRealm(event.getRealmId()));
				UserUpdateData uud = new UserUpdateData();
				uud.setId(user.getId());
				if( event.getType() == REGISTER || event.getType() == UPDATE_PROFILE ) {
					copyGeneralProperties(user, uud);
				}
				if( event.getType() == REGISTER || event.getType() == UPDATE_EMAIL ) {
					copyEmail(user, uud);
				}

				UserModificationEvent userModificationEvent = new UserModificationEvent();
				userModificationEvent.setInitiatingUserId(user.getId());
				userModificationEvent.setTimestamp(System.currentTimeMillis());
				userModificationEvent.setType(event.getType() == REGISTER ? CREATE : UPDATE);
				userModificationEvent.setPayload(uud);

				producer.send(new ProducerRecord<>(topicName, user.getId(), om.writeValueAsString(userModificationEvent))).get();
				success = true;
			}
			catch( JsonProcessingException e ) {
				throw new IllegalArgumentException(e);
			}
			catch( InterruptedException e ) {
				throw new IllegalStateException(e);
			}
			catch( ExecutionException e ) {
				throw new IllegalStateException(e.getCause());
			}
			finally {
				if( !success ) {
					session.getTransactionManager().setRollbackOnly();
				}
			}
		}
	}

	private void copyGeneralProperties(UserModel um, UserUpdateData uud) {
		uud.setUsername(um.getUsername());
		// TODO Maybe we should include first/last name and ignore them at the application level! This means having a different UserUpdateData object!
		uud.setBio(um.getFirstAttribute("bio"));
		uud.setImageUrl(um.getFirstAttribute("imageUrl"));
	}

	private void copyEmail(UserModel um, UserUpdateData uud) {
		uud.setEmail(um.getEmail());
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
		if( event.getResourceType() == USER && INTERESTING_ADMIN_OP_TYPES.contains(event.getOperationType()) ) {
			boolean success = false;
			if( !includeRepresentation ) {
				LOG.warning("includeRepresentation is false");
			}
			try {
				if( event.getRepresentation() == null ) {
					throw new IllegalArgumentException("representation is null");
				}

				JsonReader jsonReader = Json.createReader(new StringReader(event.getRepresentation()));
				JsonObject jobj = jsonReader.readObject();

				UserModificationEvent userModificationEvent = new UserModificationEvent();
				userModificationEvent.setInitiatingUserId(event.getAuthDetails().getUserId());
				userModificationEvent.setTimestamp(event.getTime());

				UserUpdateData uud = new UserUpdateData();
				uud.setId(jobj.getString("id"));
				uud.setUsername(jobj.getString("username"));

				if( event.getOperationType() == OperationType.DELETE ) {
					userModificationEvent.setType(DELETE);
				}
				else {
					userModificationEvent.setType(event.getOperationType() == OperationType.CREATE ? CREATE : UPDATE);
					uud.setEmail(jobj.getString("email"));
					getJsonValue(jobj, "attributes").ifPresent(jval -> {
						JsonObject jattributes = jval.asJsonObject();
						getJsonValue(jattributes, "imageUrl").map(JsonValue::asJsonArray).flatMap(this::first).map(jv -> ((JsonString) jv).getString()).ifPresent(uud::setImageUrl);
						getJsonValue(jattributes, "bio").map(JsonValue::asJsonArray).flatMap(this::first).map(jv -> ((JsonString) jv).getString()).ifPresent(uud::setBio);
					});
				}

				userModificationEvent.setPayload(uud);

				producer.send(new ProducerRecord<>(topicName, uud.getId(), om.writeValueAsString(userModificationEvent))).get();
				success = true;
			}
			catch( JsonProcessingException e ) {
				throw new IllegalArgumentException(e);
			}
			catch( InterruptedException e ) {
				throw new IllegalStateException(e);
			}
			catch( ExecutionException e ) {
				throw new IllegalStateException(e.getCause());
			}
			finally {
				if( !success ) {
					session.getTransactionManager().setRollbackOnly();
				}
			}
		}
	}

	private Optional<JsonValue> getJsonValue(JsonObject jobj, String name) {
		if( jobj == null || !jobj.containsKey(name) || jobj.isNull(name) ) {
			return Optional.empty();
		}
		else {
			return Optional.of(jobj.get(name));
		}
	}

	private Optional<JsonValue> first(JsonArray arr) {
		if( arr.size() > 0 ) {
			return Optional.of(arr.get(0));
		}
		else {
			return Optional.empty();
		}
	}

	@Override
	public void close() {
		// NOOP
	}
}
