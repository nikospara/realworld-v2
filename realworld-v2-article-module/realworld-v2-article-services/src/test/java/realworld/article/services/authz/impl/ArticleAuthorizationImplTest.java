package realworld.article.services.authz.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthorizedException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.Optional;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.EntityDoesNotExistException;
import realworld.NameAndId;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleUpdateData;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.NotAuthorizedException;
import realworld.authorization.service.Authorization;

/**
 * Tests for the {@link ArticleAuthorizationImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ArticleAuthorizationImplTest {

	private static final String SLUG = "SLUG";
	private static final String USER_ID = "USER_ID";
	private static final String AUTHOR_ID = "AUTHOR_ID";

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Produces @Mock
	private ArticleDao articleDao;

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private ArticleAuthorizationImpl sut;

	@Test
	void testRequireCurrentUserToBeAuthorOfUnauthenticated() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireLogin();
		expectNotAuthenticatedException(() -> sut.requireCurrentUserToBeAuthorOf(SLUG));
	}

	@Test
	void testRequireCurrentUserToBeAuthorOfWhenUserIsNotAuthor() {
		setupCurrentUser();
		setupArticleForSlugWithAuthor(AUTHOR_ID, USER_ID);
		expectNotAuthorizedException(() -> sut.requireCurrentUserToBeAuthorOf(SLUG));
	}

	@Test
	void testRequireCurrentUserToBeAuthorOfForUnknownSlugReturnsSilently() {
		setupCurrentUser();
		doThrow(EntityDoesNotExistException.class).when(articleDao).findFullDataBySlug(USER_ID, SLUG);
		sut.requireCurrentUserToBeAuthorOf(SLUG);
	}

	@Test
	void testRequireCurrentUserToBeAuthorOf() {
		setupCurrentUser();
		setupArticleForSlugWithAuthor(USER_ID);
		sut.requireCurrentUserToBeAuthorOf(SLUG);
	}

	@Test
	void testAuthorizeUpdateForNull() {
		sut.authorizeUpdate(SLUG, null);
	}

	@Test
	void testAuthorizeUpdateForUnknownSlugReturnsSilently() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class);
		doThrow(EntityDoesNotExistException.class).when(articleDao).findFullDataBySlug(USER_ID, SLUG);
		lenient().doThrow(NotAuthorizedException.class).when(authorization).requireSystemUser();
		sut.authorizeUpdate(SLUG, updateData);
	}

	@Test
	void testAuthorizeUpdateNoProtectedFieldSetNoLogin() {
		ArticleUpdateData updateData = mock(ArticleUpdateData.class, invocation -> null);
		setupArticleForSlugWithAuthor(AUTHOR_ID, null);
		doThrow(NotAuthenticatedException.class).when(authorization).requireLogin();
		expectNotAuthenticatedException(() -> sut.authorizeUpdate(SLUG, updateData));
	}

	@Test
	void testAuthorizeUpdateNoProtectedFieldSetNotAuthor() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class, invocation -> null);
		setupArticleForSlugWithAuthor(AUTHOR_ID, USER_ID);
		expectNotAuthorizedException(() -> sut.authorizeUpdate(SLUG, updateData));
	}

	@Test
	void testAuthorizeUpdateNoProtectedFieldSetUserIsAuthor() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class, invocation -> null);
		setupArticleForSlugWithAuthor(USER_ID);
		lenient().doThrow(NotAuthorizedException.class).when(authorization).requireSystemUser();
		sut.authorizeUpdate(SLUG, updateData);
	}

	@Test
	void testAuthorizeUpdateWithAuthorIdChangedUserIsAuthor() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class);
		when(updateData.getAuthorId()).thenReturn(Optional.of(AUTHOR_ID));
		setupArticleForSlugWithAuthor(USER_ID);
		doThrow(NotAuthorizedException.class).when(authorization).requireSystemUser();
		expectNotAuthorizedException(() -> sut.authorizeUpdate(SLUG, updateData));
	}

	@Test
	void testAuthorizeUpdateWithCreatedAtChangedUserIsAuthor() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class);
		when(updateData.getAuthorId()).thenReturn(null);
		when(updateData.getCreatedAt()).thenReturn(Optional.of(LocalDateTime.now()));
		setupArticleForSlugWithAuthor(USER_ID);
		doThrow(NotAuthorizedException.class).when(authorization).requireSystemUser();
		expectNotAuthorizedException(() -> sut.authorizeUpdate(SLUG, updateData));
	}

	@Test
	void testAuthorizeUpdateWithUpdatedAtChangedUserIsAuthor() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class);
		when(updateData.getAuthorId()).thenReturn(null);
		when(updateData.getCreatedAt()).thenReturn(null);
		when(updateData.getUpdatedAt()).thenReturn(Optional.of(LocalDateTime.now()));
		setupArticleForSlugWithAuthor(USER_ID);
		doThrow(NotAuthorizedException.class).when(authorization).requireSystemUser();
		expectNotAuthorizedException(() -> sut.authorizeUpdate(SLUG, updateData));
	}

	@Test
	void testAuthorizeUpdateWithAllProtectedFieldsChangedUserIsSystem() {
		setupCurrentUser();
		ArticleUpdateData updateData = mock(ArticleUpdateData.class);
		when(updateData.getAuthorId()).thenReturn(Optional.of(AUTHOR_ID));
		when(updateData.getCreatedAt()).thenReturn(Optional.of(LocalDateTime.now()));
		when(updateData.getUpdatedAt()).thenReturn(Optional.of(LocalDateTime.now()));
		setupArticleForSlugWithAuthor(USER_ID);
		doNothing().when(authorization).requireSystemUser();
		sut.authorizeUpdate(SLUG, updateData);
	}

	@Test
	void testAuthorizeDeleteForSystemUser() {
		when(authenticationContext.isSystem()).thenReturn(true);
		sut.authorizeDelete(SLUG);
	}

	@Test
	void testAuthorizeDeleteForAuthor() {
		setupCurrentUser();
		setupArticleForSlugWithAuthor(USER_ID);
		when(authenticationContext.isSystem()).thenReturn(false);
		sut.authorizeDelete(SLUG);
	}

	@Test
	void testAuthorizeDeleteForOtherUser() {
		setupCurrentUser();
		setupArticleForSlugWithAuthor(AUTHOR_ID, USER_ID);
		when(authenticationContext.isSystem()).thenReturn(false);
		expectNotAuthorizedException(() -> sut.authorizeDelete(SLUG));
	}

	private User setupCurrentUser() {
		User currentUser = mock(User.class);
		when(currentUser.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(currentUser);
		return currentUser;
	}

	private ArticleCombinedFullData setupArticleForSlugWithAuthor(String authorId) {
		return setupArticleForSlugWithAuthor(authorId, authorId);
	}

	private ArticleCombinedFullData setupArticleForSlugWithAuthor(String authorId, String userId) {
		ArticleCombinedFullData article = new ArticleCombinedFullData();
		article.setAuthor(authorId != null ? new NameAndId("", authorId) : null);
		ArticleBase base = mock(ArticleBase.class);
		article.setArticle(base);
		when(articleDao.findFullDataBySlug(userId, SLUG)).thenReturn(article);
		return article;
	}
}
