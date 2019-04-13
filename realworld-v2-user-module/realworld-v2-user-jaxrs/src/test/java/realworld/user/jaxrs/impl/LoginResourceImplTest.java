package realworld.user.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import realworld.jaxrs.sys.authentication.JwtService;
import realworld.jaxrs.sys.exceptionmap.NotAuthenticatedExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;
import realworld.user.jaxrs.LoginResource;
import realworld.user.model.ImmutableUserData;
import realworld.user.services.UserService;

/**
 * Tests for the {@link LoginResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions(ResteasyCdiExtension.class)
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class LoginResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";
	private static final String USER_ID = "userid";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "P@ssword";
	private static final String EMAIL = "userid@here.com";
	private static final String TOKEN = "TOKEN";

	@Produces @Mock
	private UserService userService;

	@Produces @Mock
	private JwtService jwtService;

	@Inject
	private LoginResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(ObjectMapperProvider.class, NotAuthenticatedExceptionMapper.class);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(LoginResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testLoginError() throws Exception {
		MockHttpRequest request = MockHttpRequest.post(APPLICATION_PATH + "/login")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN)
			.content(("{\"email\":\"" + EMAIL + "\", \"password\":\"" + PASSWORD + "\"}").getBytes());

		when(userService.findByEmailAndPassword(any(), any())).thenThrow(EntityDoesNotExistException.class);

		dispatcher.invoke(request, response);

		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> passwdCaptor = ArgumentCaptor.forClass(String.class);
		verify(userService).findByEmailAndPassword(emailCaptor.capture(), passwdCaptor.capture());
		assertEquals(EMAIL, emailCaptor.getValue());
		assertEquals(PASSWORD, passwdCaptor.getValue());

		assertEquals(401, response.getStatus());
	}

	@Test
	void testLogin() throws Exception {
		MockHttpRequest request = MockHttpRequest.post(APPLICATION_PATH + "/login")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN)
			.content(("{\"email\":\"" + EMAIL + "\", \"password\":\"" + PASSWORD + "\"}").getBytes());

		when(userService.findByEmailAndPassword(any(), any())).thenAnswer(a -> ImmutableUserData.builder().id(USER_ID).username(USERNAME).email(EMAIL).build());
		when(jwtService.toToken(USER_ID, USERNAME)).thenReturn(TOKEN);

		dispatcher.invoke(request, response);

		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> passwdCaptor = ArgumentCaptor.forClass(String.class);
		verify(userService).findByEmailAndPassword(emailCaptor.capture(), passwdCaptor.capture());
		assertEquals(EMAIL, emailCaptor.getValue());
		assertEquals(PASSWORD, passwdCaptor.getValue());

		assertEquals(200, response.getStatus());
		assertEquals(TOKEN, response.getContentAsString());
	}
}
