package realworld.article.jaxrs.impl;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.article.jaxrs.impl.ArticleCombinedFullDataDtoAssertions.assertDto;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.UUID;

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
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ImmutableArticleBase;
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

	private static final String ARTICLE_ID = UUID.randomUUID().toString();
	private static final String APPLICATION_PATH = "/api/current";
	private static final String SLUG = "slug";
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final String DESCRIPTION = "Description";
	private static final String TITLE = "Title";
	private static final LocalDateTime CREATED_AT = LocalDateTime.of(2019, Month.MARCH, 11, 16, 45, 55);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2019, Month.MARCH, 12, 16, 45, 55);
	private static final String BODY = "Body";
	private static final int FAV_COUNT = 11;

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
	void testCreate() throws Exception {
		MockHttpRequest request = MockHttpRequest.post(APPLICATION_PATH + "/articles")
			.contentType(MediaType.APPLICATION_JSON)
			.content(("{\"title\":\"" + TITLE + "\", \"description\":\"" + DESCRIPTION + "\", \"body\":\"" + BODY + "\", \"tagList\":[], \"authorId\": \"" + AUTHOR_ID + "\"}").getBytes());

		when(articleService.create(any())).then(a -> {
			ArticleBase article = mock(ArticleBase.class);
			when(article.getSlug()).thenReturn(SLUG);
			return article;
		});

		dispatcher.invoke(request, response);

		assertEquals(201, response.getStatus());
		ArgumentCaptor<ArticleCreationData> captor = ArgumentCaptor.forClass(ArticleCreationData.class);
		verify(articleService).create(captor.capture());
		ArticleCreationData cd = captor.getValue();
		assertEquals(TITLE, cd.getTitle());
		assertEquals(DESCRIPTION, cd.getDescription());
		assertEquals(BODY, cd.getBody());
		assertEquals(AUTHOR_ID, cd.getAuthorId());
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

	@Test
	void testGet() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/articles/" + SLUG)
			.accept(MediaType.APPLICATION_JSON);

		ImmutableArticleBase articleBase = ImmutableArticleBase.builder()
				.id(ARTICLE_ID)
				.title(TITLE)
				.slug(SLUG)
				.description(DESCRIPTION)
				.createdAt(CREATED_AT)
				.updatedAt(UPDATED_AT)
				.build();
		ArticleCombinedFullData fullData = new ArticleCombinedFullData();
		fullData.setArticle(articleBase);
		fullData.setFavoritesCount(FAV_COUNT);
		fullData.setFavorited(FALSE);
		fullData.setBody(BODY);
		fullData.setAuthorId(AUTHOR_ID);
		fullData.setTagList(Collections.emptyList());
		when(articleService.findFullDataBySlug(SLUG)).thenReturn(fullData);

		dispatcher.invoke(request, response);

		assertEquals(200, response.getStatus());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(articleService).findFullDataBySlug(captor.capture());
		assertEquals(SLUG, captor.getValue());
		assertDto(response)
				.assertId(ARTICLE_ID)
				.assertTitle(TITLE)
				.assertSlug(SLUG)
				.assertDescription(DESCRIPTION)
				.assertCreatedAt("2019-03-11T16:45:55.000Z");
	}
}
