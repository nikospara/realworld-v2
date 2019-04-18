package realworld.user.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import realworld.jaxrs.sys.exceptionmap.EntityDoesNotExistExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;
import realworld.user.jaxrs.UsersResource;
import realworld.user.services.BiographyService;

/**
 * Tests for the {@link BiographyResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions(ResteasyCdiExtension.class)
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class BiographyResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";
	private static final String USERNAME = "username";
	private static final String BIO = "BIO";

	@Produces @Mock
	private BiographyService biographyService;

	@Inject
	private BiographyResourceImpl sut;

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
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME + "/bio")
			.accept(MediaType.TEXT_PLAIN);

		when(biographyService.findByUserName(USERNAME)).thenThrow(EntityDoesNotExistException.class);

		dispatcher.invoke(request, response);

		assertEquals(404, response.getStatus());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(biographyService).findByUserName(captor.capture());
		assertEquals(USERNAME, captor.getValue());
	}

	@Test
	void testGet() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME + "/bio")
				.accept(MediaType.TEXT_PLAIN);

		when(biographyService.findByUserName(USERNAME)).thenReturn(BIO);

		dispatcher.invoke(request, response);

		assertEquals(200, response.getStatus());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(biographyService).findByUserName(captor.capture());
		assertEquals(USERNAME, captor.getValue());
		assertEquals(BIO, response.getContentAsString());
	}

	@Test
	void testUpdate() throws Exception {
		MockHttpRequest request = MockHttpRequest.put(APPLICATION_PATH + "/users/" + USERNAME + "/bio")
				.contentType(MediaType.TEXT_PLAIN)
				.content(BIO.getBytes());

		dispatcher.invoke(request, response);

		assertEquals(204, response.getStatus());
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
		verify(biographyService).updateByUserName(usernameCaptor.capture(), contentCaptor.capture());
		assertEquals(USERNAME, usernameCaptor.getValue());
		assertEquals(BIO, contentCaptor.getValue());
	}
}
