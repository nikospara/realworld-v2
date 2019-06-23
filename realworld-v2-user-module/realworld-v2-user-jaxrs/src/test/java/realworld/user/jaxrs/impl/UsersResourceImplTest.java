package realworld.user.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.user.jaxrs.impl.UserDataAssertions.assertUserData;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

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
import realworld.EntityDoesNotExistException;
import realworld.jaxrs.sys.ObjectMapperProvider;
import realworld.jaxrs.sys.exceptionmap.EntityDoesNotExistExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;
import realworld.user.jaxrs.UsersResource;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
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
	private static final String EMAIL = "userid@here.com";
	private static final String IMAGE_URL = "http://pictures.com/image1";

	@Produces @Mock
	private UserService userService;

	@Inject
	private ObjectMapperProvider objectMapperProvider;

	@Inject
	private UsersResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(ObjectMapperProvider.class, EntityDoesNotExistExceptionMapper.class);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(UsersResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testGetForNonExistingUser() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME)
			.accept(MediaType.APPLICATION_JSON);

		when(userService.findByUserName(USERNAME)).thenThrow(EntityDoesNotExistException.class);

		dispatcher.invoke(request, response);

		assertEquals(404, response.getStatus());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(userService).findByUserName(captor.capture());
		assertEquals(USERNAME, captor.getValue());
	}

	@Test
	void testGet() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME)
			.accept(MediaType.APPLICATION_JSON);

		when(userService.findByUserName(USERNAME)).thenAnswer(a -> makeUser(a.getArgument(0)));

		dispatcher.invoke(request, response);

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(userService).findByUserName(captor.capture());
		assertEquals(USERNAME, captor.getValue());

		assertUserData(response)
				.assertId(USER_ID)
				.assertUsername(USERNAME)
				.assertEmail(EMAIL)
				.assertImageUrl(IMAGE_URL);
	}

	private UserData makeUser(String username) {
		return ImmutableUserData.builder()
				.id(USER_ID)
				.username(username)
				.email(EMAIL)
				.imageUrl(IMAGE_URL)
				.build();
	}
}
