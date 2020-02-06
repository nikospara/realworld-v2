package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.SearchResult;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.service.Authorization;

/**
 * Tests for the {@link ArticleServiceAuthorizer}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ArticleServiceAuthorizerImplTest {

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

	@Produces @Mock
	private Authorization authorization;

	@Produces @Mock
	private ArticleAuthorization articleAuthorization;

	@Inject
	private ArticleServiceAuthorizerImpl sut;

	@Test
	void testCreateForInvalidUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireUserId(AUTHOR_ID);
		@SuppressWarnings("unchecked")
		Function<ArticleCreationData, ArticleBase> mockDelegate = mock(Function.class);
		expectNotAuthenticatedException(() -> sut.create(ARTICLE_CREATION_DATA, mockDelegate));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	void testCreateForValidUser() {
		@SuppressWarnings("unchecked")
		Function<ArticleCreationData, ArticleBase> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(ARTICLE_CREATION_DATA)).thenReturn(FROM_CREATE);
		ArticleBase result = sut.create(ARTICLE_CREATION_DATA, mockDelegate);
		assertSame(FROM_CREATE, result);
		verify(mockDelegate).apply(ARTICLE_CREATION_DATA);
	}

	@Test
	void testFindFullDataBySlug() {
		@SuppressWarnings("unchecked")
		Function<String, ArticleCombinedFullData> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(SLUG_TO_FIND_COMBINED_DATA)).thenReturn(FROM_FIND_FULL_DATA_BY_SLUG);
		ArticleCombinedFullData result = sut.findFullDataBySlug(SLUG_TO_FIND_COMBINED_DATA, mockDelegate);
		assertSame(FROM_FIND_FULL_DATA_BY_SLUG, result);
		verifyNoMoreInteractions(authorization);
		verifyNoMoreInteractions(articleAuthorization);
	}

	@Test
	void testFind() {
		@SuppressWarnings("unchecked")
		Function<ArticleSearchCriteria, SearchResult<ArticleSearchResult>> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(FIND_CRITERIA)).thenReturn(FROM_FIND);
		SearchResult<ArticleSearchResult> result = sut.find(FIND_CRITERIA, mockDelegate);
		assertSame(FROM_FIND, result);
		verifyNoMoreInteractions(authorization);
		verifyNoMoreInteractions(articleAuthorization);
	}

	@Test
	void testUpdate() {
		@SuppressWarnings("unchecked")
		BiFunction<String, ArticleUpdateData, String> mockDelegate = mock(BiFunction.class);
		ArticleUpdateData mockUpdateData = mock(ArticleUpdateData.class);
		when(mockDelegate.apply(SLUG,mockUpdateData)).thenReturn(FROM_UPDATE);
		String result = sut.update(SLUG, mockUpdateData, mockDelegate);
		assertSame(FROM_UPDATE, result);
		verify(articleAuthorization).authorizeUpdate(SLUG,mockUpdateData);
		verifyNoMoreInteractions(authorization);
		verifyNoMoreInteractions(articleAuthorization);
	}
}
