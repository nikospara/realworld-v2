package realworld.keycloak.listener;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * Set up the hook to Keycloak's events to notify Kafka about user events.
 *
 * @see RealworldEventListenerProvider The RealworldEventListenerProvider for the actual event handling logic.
 */
public class RealworldEventListenerProviderFactory implements EventListenerProviderFactory {

	private static final Logger LOG = Logger.getLogger(RealworldEventListenerProviderFactory.class.getName());

	private String topicName;
	private Producer<String, String> producer;

	@Override
	public String getId() {
		return "realworld";
	}

	@Override
	public void init(Config.Scope config) {
		topicName = config.get("topic-name");
		String bootstrapServers = config.get("bootstrap-servers");
		LOG.info("Initializing the RealworldEventListenerProviderFactory for topic: " + topicName + ", bootstrap servers: " + bootstrapServers);

		Properties props = new Properties();
		props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ACKS_CONFIG, "all");
		props.put(KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
		props.put(VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
		producer = new KafkaProducer<>(props);
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		// NOOP
	}

	@Override
	public EventListenerProvider create(KeycloakSession session) {
		LOG.info("Creating the RealworldEventListenerProvider");
		return new RealworldEventListenerProvider(session, producer, topicName);
	}

	@Override
	public void close() {
		producer.close();
	}
}
