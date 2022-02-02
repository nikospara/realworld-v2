package realworld.test.quarkus;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Start Kafka as a test resource, implement injection of the Kafka test container, expose settings to the Quarkus test environment.
 */
public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

	private KafkaContainer kafka;

	@Override
	public Map<String, String> start() {
		// see https://docs.confluent.io/platform/current/installation/versions-interoperability.html
		kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"));
		kafka.start();
		return Collections.singletonMap("mp.messaging.incoming.users-stream.bootstrap.servers", kafka.getBootstrapServers());
	}

	@Override
	public void stop() {
		kafka.stop();
	}

	@Override
	public void inject(TestInjector testInjector) {
		testInjector.injectIntoFields(kafka, new TestInjector.AnnotatedAndMatchesType(InjectKafka.class, KafkaContainer.class));
	}

	public static Producer<String, String> makeProducer(KafkaContainer kafka) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafka.getBootstrapServers());
		props.put("acks", "all");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return new KafkaProducer<>(props);
	}
}
