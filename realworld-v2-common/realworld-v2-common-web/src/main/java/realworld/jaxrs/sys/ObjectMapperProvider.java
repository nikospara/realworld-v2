package realworld.jaxrs.sys;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Provide the Jackson {@code ObjectMapper} to JAX-RS,
 * customized for the needs of the application.
 *
 * @see <a href="#">com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider</a>
 * @see <a href="#">com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider</a>
 * @see <a href="#">com.fasterxml.jackson.jaxrs.json.JsonMapperConfigurator</a>
 */
@Provider
@ApplicationScoped
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

	protected ObjectMapper objectMapper;

	@PostConstruct
	void init() {
		objectMapper = new ObjectMapper();
		objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new CustomJavaTimeModule());
//		SimpleDateFormat traditional8601 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"); // NOTE: This is cloned, so it is thread safe
//		traditional8601.setTimeZone(TimeZone.getTimeZone("UTC"));
//		objectMapper.setDateFormat(traditional8601);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return objectMapper;
	}
}
