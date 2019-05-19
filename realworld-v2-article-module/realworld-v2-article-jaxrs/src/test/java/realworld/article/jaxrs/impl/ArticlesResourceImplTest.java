package realworld.article.jaxrs.impl;

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
import realworld.article.jaxrs.ArticlesResource;
import realworld.article.services.ArticleService;
import realworld.jaxrs.sys.ObjectMapperProvider;
import realworld.jaxrs.sys.exceptionmap.EntityDoesNotExistExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;

/**
 * Tests for the {@link ArticlesResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions(ResteasyCdiExtension.class)
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class ArticlesResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";
	private static final String SLUG = "slug";

	@Produces
	@Mock
	private ArticleService articleService;

	@Inject
	private ObjectMapperProvider objectMapperProvider;

	@Inject
	private ArticlesResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(ObjectMapperProvider.class, EntityDoesNotExistExceptionMapper.class);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(ArticlesResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testGetNonExistingSlug() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/articles/" + SLUG)
			.accept(MediaType.APPLICATION_JSON);

		when(articleService.findFullDataBySlug(SLUG)).thenThrow(EntityDoesNotExistException.class);

		dispatcher.invoke(request, response);

		assertEquals(404, response.getStatus());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(articleService).findFullDataBySlug(captor.capture());
		assertEquals(SLUG, captor.getValue());
	}
}
