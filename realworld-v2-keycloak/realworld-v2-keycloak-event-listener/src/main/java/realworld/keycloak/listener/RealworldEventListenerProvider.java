package realworld.keycloak.listener;

import static org.keycloak.events.EventType.REGISTER;
import static org.keycloak.events.EventType.UPDATE_EMAIL;
import static org.keycloak.events.EventType.UPDATE_PROFILE;
import static org.keycloak.events.admin.ResourceType.USER;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

/**
 * The Keycloak event handler that actually forwards the event to Kafka.
 *
 * @see RealworldEventListenerProviderFactory The RealworldEventListenerProviderFactory for the configuration logic.
 */
class RealworldEventListenerProvider implements EventListenerProvider {

	private static final Logger LOG = Logger.getLogger(RealworldEventListenerProvider.class.getName());

	private static final EnumSet<EventType> INTERESTING_EVENT_TYPES = EnumSet.of(REGISTER, UPDATE_EMAIL, UPDATE_PROFILE);
	private static final EnumSet<OperationType> INTERESTING_ADMIN_OP_TYPES = EnumSet.of(OperationType.CREATE, OperationType.DELETE, OperationType.UPDATE);

	private final KeycloakSession session;
	private final Producer<String, String> producer;
	private final String topicName;

	RealworldEventListenerProvider(KeycloakSession session, Producer<String, String> producer, String topicName) {
		this.session = session;
		this.producer = producer;
		this.topicName = topicName;
	}

	@Override
	public void onEvent(Event event) {
		if( INTERESTING_EVENT_TYPES.contains(event.getType()) ) {
			boolean success = false;
			try {
				UserModel user = session.users().getUserById(session.realms().getRealm(event.getRealmId()), event.getUserId());
				JsonObjectBuilder userUpdateDataBuilder = Json.createObjectBuilder();
				userUpdateDataBuilder.add("id", user.getId());
				if( event.getType() == REGISTER || event.getType() == UPDATE_PROFILE ) {
					copyGeneralProperties(user, userUpdateDataBuilder);
				}
				if( event.getType() == REGISTER || event.getType() == UPDATE_EMAIL ) {
					copyEmail(user, userUpdateDataBuilder);
				}

				JsonObjectBuilder userModificationEventBuilder = Json.createObjectBuilder()
						.add("initiatingUserId", user.getId())
						.add("timestamp", event.getTime())
						.add("type", event.getType() == REGISTER ? "CREATE" : "UPDATE")
						.add("payload", userUpdateDataBuilder);

				producer.send(new ProducerRecord<>(topicName, user.getId(), userModificationEventBuilder.build().toString())).get();
				success = true;
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

	private void copyGeneralProperties(UserModel um, JsonObjectBuilder userUpdateDataBuilder) {
		// TODO Maybe we should include first/last name and ignore them at the application level! This means having a different UserUpdateData object!
		userUpdateDataBuilder.add("username", um.getUsername());
		Optional.ofNullable(um.getFirstAttribute("bio")).ifPresent(bio -> userUpdateDataBuilder.add("bio", bio));
		Optional.ofNullable(um.getFirstAttribute("imageUrl")).ifPresent(imageUrl -> userUpdateDataBuilder.add("imageUrl", imageUrl));
	}

	private void copyEmail(UserModel um, JsonObjectBuilder userUpdateDataBuilder) {
		userUpdateDataBuilder.add("email", um.getEmail());
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
		if( event.getResourceType() == USER && INTERESTING_ADMIN_OP_TYPES.contains(event.getOperationType()) ) {
			boolean success = false;
			if( !includeRepresentation ) {
				// TODO On DELETE, representation will be null and includeRepresentation false!
				LOG.warning("includeRepresentation is false");
			}
			try {
				if( event.getRepresentation() == null ) {
					throw new IllegalArgumentException("representation is null");
				}

				JsonReader jsonReader = Json.createReader(new StringReader(event.getRepresentation()));
				JsonObject jobj = jsonReader.readObject();

				JsonObjectBuilder userModificationEventBuilder = Json.createObjectBuilder();
				userModificationEventBuilder.add("initiatingUserId", event.getAuthDetails().getUserId());
				userModificationEventBuilder.add("timestamp", event.getTime());

				JsonObjectBuilder userUpdateDataBuilder = Json.createObjectBuilder();
				String userId = jobj.getString("id");
				userUpdateDataBuilder.add("id", userId);
				userUpdateDataBuilder.add("username", jobj.getString("username"));

				if( event.getOperationType() == OperationType.DELETE ) {
					userModificationEventBuilder.add("type", "DELETE");
				}
				else {
					userModificationEventBuilder.add("type", event.getOperationType().name());
					userUpdateDataBuilder.add("email", jobj.getString("email"));
					getJsonValue(jobj, "attributes").ifPresent(jval -> {
						JsonObject jattributes = jval.asJsonObject();
						getJsonValue(jattributes, "imageUrl").map(JsonValue::asJsonArray).flatMap(this::first).map(jv -> ((JsonString) jv).getString()).ifPresent(imageUrl -> userUpdateDataBuilder.add("imageUrl", imageUrl));
						getJsonValue(jattributes, "bio").map(JsonValue::asJsonArray).flatMap(this::first).map(jv -> ((JsonString) jv).getString()).ifPresent(bio -> userUpdateDataBuilder.add("bio", bio));
					});
				}

				userModificationEventBuilder.add("payload", userUpdateDataBuilder);

				producer.send(new ProducerRecord<>(topicName, userId, userModificationEventBuilder.build().toString())).get();
				success = true;
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
		return Optional.of(jobj)
				.filter(jo -> jo.containsKey(name))
				.filter(jo -> !jo.isNull(name))
				.map(jo -> jo.get(name));
	}

	private Optional<JsonValue> first(JsonArray arr) {
		return Optional.of(arr)
				.filter(a -> a.size() > 0)
				.map(a -> a.get(0));
	}

	@Override
	public void close() {
		// NOOP
	}
}
