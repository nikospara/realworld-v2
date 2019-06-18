package realworld.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson's {@code ObjectMapper} utilities common to the entire application.
 * <p>
 * Remember that, as per its Javadocs, the {@code ObjectMapper} is thread-safe,
 * as long as ALL configuration of the instance occurs before ANY read or write calls.
 */
public abstract class ObjectMapperUtils {

	private ObjectMapperUtils() {
		// DISABLE INSTANTIATION
	}

	/**
	 * Create a new {@code ObjectMapper}.
	 *
	 * @return A new {@code ObjectMapper}
	 */
	public static ObjectMapper createObjectMapper() {
		return new ObjectMapper();
	}

	/**
	 * Apply any necessary customizations to the given {@code ObjectMapper}.
	 *
	 * @param om The {@code ObjectMapper} to customize
	 */
	public static void customize(ObjectMapper om) {
		om.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
		om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		om.registerModule(new JavaTimeModule());
		om.registerModule(new CustomJavaTimeModule());
	}
}
