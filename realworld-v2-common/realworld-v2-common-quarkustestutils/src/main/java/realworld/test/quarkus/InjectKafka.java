package realworld.test.quarkus;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Inject the Kafka test container in Quarkus tests.
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD})
public @interface InjectKafka {
}
