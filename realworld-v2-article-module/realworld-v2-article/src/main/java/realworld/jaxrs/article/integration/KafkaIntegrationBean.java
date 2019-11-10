package realworld.jaxrs.article.integration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realworld.article.services.UserService;
import realworld.authentication.AuthenticationContextImpl;
import realworld.authentication.AuthenticationContextProducer;
import realworld.user.model.events.UserModificationEvent;

/**
 * Listens to user events from Kafka and takes appropriate action.
 */
@ApplicationScoped
public class KafkaIntegrationBean {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaIntegrationBean.class);

	private Supplier<ObjectMapper> objectMapperSupplier;

	private UserService userService;

	private AuthenticationContextProducer authenticationContextProducer;

	/**
	 * Constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	KafkaIntegrationBean() {
		// NOOP
	}

	@Inject
	public KafkaIntegrationBean(Supplier<ObjectMapper> objectMapperSupplier, UserService userService, AuthenticationContextProducer authenticationContextProducer) {
		this.objectMapperSupplier = objectMapperSupplier;
		this.userService = userService;
		this.authenticationContextProducer = authenticationContextProducer;
	}

	@Incoming("users-stream")
	@SuppressWarnings("unused")
	public CompletionStage<Void> onUsersEvent(KafkaMessage<String,String> message) {
		return CompletableFuture.runAsync(() -> {
			authenticationContextProducer.pushContext(AuthenticationContextImpl.system());
			try {
				UserModificationEvent event = objectMapperSupplier.get().readValue(message.getPayload(), UserModificationEvent.class);
				LOG.info("Received user event from Kafka of type {}", event.getType());
				switch (event.getType()) {
					case CREATE:
						userService.add(event.getPayload().getId(), event.getPayload().getUsername());
						break;
					case UPDATE:
						userService.updateUsername(event.getPayload().getId(), event.getPayload().getUsername());
						break;
				}
			}
			catch( Exception e ) {
				LOG.error("Error while processing Kafka user event: " + message.getKey(), e);
			}
			finally {
				authenticationContextProducer.popContext();
			}
		});
	}
}
