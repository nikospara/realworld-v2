package realworld.user.jaxrs.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.test.jaxrs.CustomMockDispatcherFactory;
import realworld.user.jaxrs.UsersResource;
import realworld.user.services.UserService;

/**
 * Tests for the {@link UsersResourceImpl}.
 */
@EnableAutoWeld
@AddExtensions(ResteasyCdiExtension.class)
@ExtendWith(MockitoExtension.class)
public class UsersResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";

	@Produces
	@Mock
	private UserService userService;

	@Inject
	private UsersResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(null/*ObjectMapperProvider.class*/);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(UsersResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testRegister() throws Exception {

	}
}
