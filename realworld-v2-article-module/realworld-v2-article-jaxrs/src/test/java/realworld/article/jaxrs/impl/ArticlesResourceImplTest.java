package realworld.article.jaxrs.impl;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.article.jaxrs.impl.ArticleCombinedFullDataDtoAssertions.assertDto;
import static realworld.article.jaxrs.impl.ArticleSearchResultsDtoAssertions.assertSearchResultsDto;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.smallrye.config.inject.ConfigExtension;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.Dispatcher;
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
import realworld.NameAndId;
import realworld.SearchResult;
import realworld.article.jaxrs.ArticlesResource;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.article.model.ImmutableArticleBase;
import realworld.article.model.ImmutableArticleSearchResult;
import realworld.article.services.ArticleService;
import realworld.jaxrs.sys.ObjectMapperProvider;
import realworld.jaxrs.sys.exceptionmap.EntityDoesNotExistExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;

/**
 * Tests for the {@link ArticlesResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions({ResteasyCdiExtension.class, ConfigExtension.class})
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class ArticlesResourceImplTest {

	private static final String ARTICLE_ID = UUID.randomUUID().toString();
	private static final String APPLICATION_PATH = "/api/current";
	private static final String SLUG = "slug";
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final String AUTHOR_NAME = "AUTHOR_NAME";
	private static final String DESCRIPTION = "Description";
	private static final String TITLE = "Title";
	private static final LocalDateTime CREATED_AT = LocalDateTime.of(2019, Month.MARCH, 11, 16, 45, 55);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2019, Month.MARCH, 12, 16, 45, 55);
	private static final String BODY = "Body";
	private static final int FAV_COUNT = 11;
	private static final Set<String> TAG_LIST = new HashSet<>(Arrays.asList("tag1", "tag2"));

	@Produces
	private ArticleRestLayerConfig config = () -> "http://user-server/api/v2/users/{username}";

	@Produces @Mock
	private ArticleService articleService;

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
	void testUpdate() throws Exception {
		MockHttpRequest request = MockHttpRequest.put(APPLICATION_PATH + "/articles/" + SLUG)
			.contentType(MediaType.APPLICATION_JSON)
			.content(("{\"title\":\"" + TITLE + "\", \"body\":\"" + BODY + "\", \"tagList\":[\"tag1\", \"tag2\"], \"authorId\": \"" + AUTHOR_ID + "\"}").getBytes());

		when(articleService.update(eq(SLUG), any())).thenReturn(ARTICLE_ID);

		dispatcher.invoke(request, response);

		assertEquals(204, response.getStatus());
		ArgumentCaptor<ArticleUpdateData> captor = ArgumentCaptor.forClass(ArticleUpdateData.class);
		verify(articleService).update(eq(SLUG), captor.capture());
		ArticleUpdateData ud = captor.getValue();
		assertEquals(TITLE, ud.getTitle().orElseThrow());
		assertNull(ud.getDescription());
		assertEquals(BODY, ud.getBody().orElseThrow());
		assertEquals(TAG_LIST, ud.getTagList().orElseThrow());
		assertEquals(AUTHOR_ID, ud.getAuthorId().orElseThrow());
	}

	@Test
	void testDelete() throws Exception {
		MockHttpRequest request = MockHttpRequest.delete(APPLICATION_PATH + "/articles/" + SLUG);

		dispatcher.invoke(request, response);

		assertEquals(204, response.getStatus());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(articleService).delete(captor.capture());
		assertEquals(SLUG, captor.getValue());
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

		ArticleBase articleBase = makeArticleBase();
		ArticleCombinedFullData fullData = new ArticleCombinedFullData();
		fullData.setArticle(articleBase);
		fullData.setFavoritesCount(FAV_COUNT);
		fullData.setFavorited(FALSE);
		fullData.setBody(BODY);
		fullData.setAuthor(new NameAndId(AUTHOR_NAME, AUTHOR_ID));
		fullData.setTagList(TAG_LIST);
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
				.assertAuthorName(AUTHOR_NAME)
				.assertAuthorHref("http://user-server/api/v2/users/" + AUTHOR_NAME)
				.assertCreatedAt("2019-03-11T16:45:55.000Z")
				.assertTagList("tag1", "tag2");
	}

	@Test
	void testFind() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/articles?tag=TTT&author=123&favorited=456&limit=8&offset=9")
				.accept(MediaType.APPLICATION_JSON);

		ArticleBase articleBase = makeArticleBase();
		ArticleSearchResult articleSearchResult = ImmutableArticleSearchResult.builder()
				.article(articleBase)
				.addAllTagList(TAG_LIST)
				.isFavorited(FALSE)
				.favoritesCount(FAV_COUNT)
				.author(new NameAndId(AUTHOR_NAME, AUTHOR_ID))
				.build();
		SearchResult<ArticleSearchResult> searchResults = new SearchResult<>(1, Collections.singletonList(articleSearchResult));
		when(articleService.find(any(ArticleSearchCriteria.class))).thenReturn(searchResults);

		dispatcher.invoke(request, response);

		assertEquals(200, response.getStatus());
		ArgumentCaptor<ArticleSearchCriteria> captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);
		verify(articleService).find(captor.capture());
		ArticleSearchCriteria criteria = captor.getValue();
		assertEquals("TTT", criteria.getTag());
		assertEquals("123", criteria.getAuthors().get(0));
		assertEquals(1, criteria.getAuthors().size());
		assertEquals("456", criteria.getFavoritedBy());
		assertEquals(8, criteria.getLimit());
		assertEquals(9, criteria.getOffset());

		assertSearchResultsDto(response)
				.assertCount(1)
				.nextResult()
				.assertId(ARTICLE_ID)
				.assertTitle(TITLE)
				.assertSlug(SLUG)
				.assertDescription(DESCRIPTION)
				.assertCreatedAt("2019-03-11T16:45:55.000Z")
				.assertUpdatedAt("2019-03-12T16:45:55.000Z")
				.assertTagList("tag1", "tag2");
	}

	private ArticleBase makeArticleBase() {
		return ImmutableArticleBase.builder()
				.id(ARTICLE_ID)
				.title(TITLE)
				.slug(SLUG)
				.description(DESCRIPTION)
				.createdAt(CREATED_AT)
				.updatedAt(UPDATED_AT)
				.build();
	}
}
