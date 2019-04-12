package realworld.user.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.jaxrs.sys.ObjectMapperProvider;
import realworld.test.jaxrs.CustomMockDispatcherFactory;
import realworld.user.jaxrs.UsersResource;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.services.UserService;

/**
 * Tests for the {@link UsersResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions(ResteasyCdiExtension.class)
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class UsersResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";
	private static final String USER_ID = "userid";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "P@ssword";
	private static final String EMAIL = "userid@here.com";
	private static final String IMAGE_URL = "http://pictures.com/image1";

	@Produces
	@Mock
	private UserService userService;

	@Inject
	private ObjectMapperProvider objectMapperProvider;

	@Inject
	private UsersResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(ObjectMapperProvider.class);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(UsersResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testRegister() throws Exception {
		MockHttpRequest request = MockHttpRequest.post(APPLICATION_PATH + "/users");
		request.contentType(MediaType.APPLICATION_JSON);
		request.accept(MediaType.APPLICATION_JSON);
		request.content(("{\"email\":\"" + EMAIL + "\", \"password\":\"" + PASSWORD + "\", \"username\":\"" + USERNAME + "\"}").getBytes());

		when(userService.register(any())).thenAnswer(a -> {
			UserRegistrationData r = a.getArgument(0);
			return makeUser(r.getUsername(), r.getEmail(), null);
		});

		dispatcher.invoke(request, response);

		ArgumentCaptor<UserRegistrationData> captor = ArgumentCaptor.forClass(UserRegistrationData.class);
		verify(userService).register(captor.capture());
		assertEquals(PASSWORD, captor.getValue().getPassword());

		assertEquals(201, response.getStatus());
		assertEquals(UriBuilder.fromPath(APPLICATION_PATH).segment("users", "{username}").build(USERNAME), response.getOutputHeaders().getFirst("Location"));
	}

	private UserData makeUser(String username, String email, String image) {
		return ImmutableUserData.builder()
				.id(USER_ID)
				.username(username)
				.email(email)
				.imageUrl(image)
				.build();
	}
}
