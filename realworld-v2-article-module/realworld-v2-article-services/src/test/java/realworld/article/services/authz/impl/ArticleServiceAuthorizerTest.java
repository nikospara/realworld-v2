package realworld.article.services.authz.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthorizedException;

import javax.inject.Inject;
import java.util.UUID;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.SearchResult;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.article.services.ArticleService;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.NotAuthorizedException;

/**
 * Tests for the {@link ArticleServiceAuthorizer}.
 */
@EnableAutoWeld
@AddEnabledDecorators(ArticleServiceAuthorizer.class)
@AddBeanClasses({ArticleServiceAuthorizerTest.DummyArticleService.class, ArticleServiceAuthorizerDependenciesProducer.class})
@ExtendWith(MockitoExtension.class)
public class ArticleServiceAuthorizerTest {

	public static class DummyArticleService implements ArticleService {
		@Override
		public ArticleBase create(ArticleCreationData creationData) {
			return FROM_CREATE;
		}

		@Override
		public String update(String slug, ArticleUpdateData updateData) {
			return FROM_UPDATE;
		}

		@Override
		public void delete(String slug) {

		}

		@Override
		public ArticleCombinedFullData findFullDataBySlug(String slug) {
			return FROM_FIND_FULL_DATA_BY_SLUG;
		}

		@Override
		public SearchResult<ArticleSearchResult> find(ArticleSearchCriteria criteria) {
			return FROM_FIND;
		}
	}

	private static final String SLUG_TO_FIND_COMBINED_DATA = "SLUG_TO_FIND_COMBINED_DATA";
	private static final ArticleCombinedFullData FROM_FIND_FULL_DATA_BY_SLUG = new ArticleCombinedFullData();
	private static final ArticleCreationData ARTICLE_CREATION_DATA = mock(ArticleCreationData.class);
	private static final ArticleBase FROM_CREATE = mock(ArticleBase.class);
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final ArticleSearchCriteria FIND_CRITERIA = mock(ArticleSearchCriteria.class);
	@SuppressWarnings("unchecked")
	private static final SearchResult<ArticleSearchResult> FROM_FIND = mock(SearchResult.class);
	private static final String SLUG = "SLUG";
	private static final String FROM_UPDATE = "from update";

	static {
		when(ARTICLE_CREATION_DATA.getAuthorId()).thenReturn(AUTHOR_ID);
	}

	@Inject
	private ArticleServiceAuthorizerDependenciesProducer dependenciesProducer;

	@Inject
	private ArticleService sut;

	@Test
	void testCreateForInvalidUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireUserId(AUTHOR_ID);
		expectNotAuthenticatedException(() -> sut.create(ARTICLE_CREATION_DATA));
		verifyNoMoreInteractions(dependenciesProducer.getArticleAuthorization());
	}

	@Test
	void testCreateForValidUser() {
		ArticleBase result = sut.create(ARTICLE_CREATION_DATA);
		assertSame(FROM_CREATE, result);
		verify(dependenciesProducer.getAuthorization()).requireUserId(AUTHOR_ID);
	}

	@Test
	void testFindFullDataBySlug() {
		ArticleCombinedFullData result = sut.findFullDataBySlug(SLUG_TO_FIND_COMBINED_DATA);
		assertSame(FROM_FIND_FULL_DATA_BY_SLUG, result);
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
		verifyNoMoreInteractions(dependenciesProducer.getArticleAuthorization());
	}

	@Test
	void testFind() {
		SearchResult<ArticleSearchResult> result = sut.find(FIND_CRITERIA);
		assertSame(FROM_FIND, result);
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
		verifyNoMoreInteractions(dependenciesProducer.getArticleAuthorization());
	}

	@Test
	void testUpdate() {
		ArticleUpdateData mockUpdateData = mock(ArticleUpdateData.class);
		String result = sut.update(SLUG, mockUpdateData);
		assertSame(FROM_UPDATE, result);
		verify(dependenciesProducer.getArticleAuthorization()).authorizeUpdate(SLUG,mockUpdateData);
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
		verifyNoMoreInteractions(dependenciesProducer.getArticleAuthorization());
	}

	@Test
	void testDeleteForInvalidUser() {
		doThrow(NotAuthorizedException.class).when(dependenciesProducer.getArticleAuthorization()).authorizeDelete(SLUG);
		expectNotAuthorizedException(() -> sut.delete(SLUG));
	}

	@Test
	void testDeleteForValidUser() {
		doNothing().when(dependenciesProducer.getArticleAuthorization()).authorizeDelete(SLUG);
		sut.delete(SLUG);
		verify(dependenciesProducer.getArticleAuthorization()).authorizeDelete(SLUG);
	}
}
