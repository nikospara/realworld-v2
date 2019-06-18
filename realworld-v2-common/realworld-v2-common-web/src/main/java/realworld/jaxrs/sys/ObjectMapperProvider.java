package realworld.jaxrs.sys;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import realworld.json.ObjectMapperUtils;

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
		objectMapper = ObjectMapperUtils.createObjectMapper();
		ObjectMapperUtils.customize(objectMapper);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return objectMapper;
	}
}
