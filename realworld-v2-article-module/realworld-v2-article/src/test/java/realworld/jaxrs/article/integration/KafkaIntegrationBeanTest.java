package realworld.jaxrs.article.integration;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import realworld.article.services.UserService;
import realworld.authentication.AuthenticationContextProducer;
import realworld.test.quarkus.InjectKafka;
import realworld.test.quarkus.KafkaTestResource;
import realworld.test.quarkus.PostgresTestResource;

/**
 * Tests for the {@link KafkaIntegrationBean}.
 */
@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
@QuarkusTestResource(PostgresTestResource.class)
public class KafkaIntegrationBeanTest {

	private static final String USERNAME1 = "bob";
	private static final String USERNAME2 = "spongebob";

	@InjectMock
	UserService userService;

	@InjectMock
	AuthenticationContextProducer authenticationContextProducer;

	@InjectKafka
	KafkaContainer kafka;

	@Test
	public void testReceivingEvents() throws Exception {
		CompletableFuture<Void> mark = new CompletableFuture<>();
		String userId = UUID.randomUUID().toString();
		doAnswer(a -> {
			mark.complete(null);
			return null;
		}).when(userService).updateUsername(userId, USERNAME2);

		try( var producer = KafkaTestResource.makeProducer(kafka) ) {
			producer.send(new ProducerRecord<>("users", userId, "{\"timestamp\":10, \"type\": \"CREATE\", \"payload\": {\"id\": \"" + userId + "\", \"username\": \"" + USERNAME1 + "\"}}"));
			producer.send(new ProducerRecord<>("users", userId, "{\"timestamp\":20, \"type\": \"UPDATE\", \"payload\": {\"id\": \"" + userId + "\", \"username\": \"" + USERNAME2 + "\"}}"));
		}

		mark.get(5, TimeUnit.SECONDS);
		verify(userService).add(userId, USERNAME1);
		verify(userService).updateUsername(userId, USERNAME2);
	}

	@Test
	public void testServiceThrows() throws Exception {
		CompletableFuture<Void> mark = new CompletableFuture<>();
		String userId = UUID.randomUUID().toString();
		AtomicBoolean calledOnce = new AtomicBoolean(false);
		doAnswer(a -> {
			if( calledOnce.get() ) {
				return null;
			}
			else {
				calledOnce.set(true);
				throw new RuntimeException("BOOM");
			}
		}).when(userService).add(userId, USERNAME1);
		doAnswer(a -> {
			mark.complete(null);
			return null;
		}).when(userService).updateUsername(userId, USERNAME2);

		try( var producer = KafkaTestResource.makeProducer(kafka) ) {
			producer.send(new ProducerRecord<>("users", userId, "{\"timestamp\":10, \"type\": \"CREATE\", \"payload\": {\"id\": \"" + userId + "\", \"username\": \"" + USERNAME1 + "\"}}"));
			producer.send(new ProducerRecord<>("users", userId, "{\"timestamp\":20, \"type\": \"UPDATE\", \"payload\": {\"id\": \"" + userId + "\", \"username\": \"" + USERNAME2 + "\"}}"));
		}

		mark.get(5, TimeUnit.SECONDS);
		verify(userService).add(userId, USERNAME1);
		verify(userService).updateUsername(userId, USERNAME2);
	}
}
