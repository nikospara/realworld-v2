package realworld.user.integration;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realworld.user.model.events.UserModificationEvent;


@ApplicationScoped
@WebListener
public class KafkaIntegrationBean implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaIntegrationBean.class);

	@Resource
	private ManagedExecutorService executorService;

	@Inject
	private Supplier<ObjectMapper> objectMapperSupplier;

	private KafkaConsumer<String, String> consumer;

	private AtomicBoolean closed;

	private Future<?> processorResult;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Properties props = new Properties();
		props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.setProperty(GROUP_ID_CONFIG, "realworld-v2-user");
		props.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.setProperty(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
		props.put(VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);

		consumer = new KafkaConsumer<>(props);

		closed = new AtomicBoolean(false);

		processorResult = executorService.submit(new MessageProcessor(consumer, closed, objectMapperSupplier));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		closed.set(true);
		consumer.wakeup();
		try {
			processorResult.get(5, TimeUnit.SECONDS);
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	private static class MessageProcessor implements Runnable {

		private KafkaConsumer<String, String> consumer;

		private AtomicBoolean closed;

		private Supplier<ObjectMapper> objectMapperSupplier;

		MessageProcessor(KafkaConsumer<String, String> consumer, AtomicBoolean closed, Supplier<ObjectMapper> objectMapperSupplier) {
			this.consumer = consumer;
			this.closed = closed;
			this.objectMapperSupplier = objectMapperSupplier;
		}

		@Override
		public void run() {
			try {
				consumer.subscribe(Collections.singletonList("users"));
				while( !closed.get() ) {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
					for (ConsumerRecord<String, String> record : records) {
						System.out.println("**************************************************************");
						System.out.println(record.key());
						System.out.println(record.value());
						try {
							UserModificationEvent event = objectMapperSupplier.get().readValue(record.value(), UserModificationEvent.class);
							System.out.println(event);
						}
						catch( IOException e ) {
							LOG.error("Error deserializing event", e);
						}
						System.out.println("**************************************************************");
					}
				}
			}
			catch( WakeupException e ) {
				if (!closed.get()) throw e;
			}
			finally {
				LOG.debug("Closing Kafka consumer");
				consumer.close();
			}
		}
	}
}
