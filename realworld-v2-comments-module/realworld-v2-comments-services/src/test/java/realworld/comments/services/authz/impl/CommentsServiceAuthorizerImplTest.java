package realworld.comments.services.authz.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthorizedException;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.Paging;
import realworld.SearchResult;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.NotAuthorizedException;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentCreationData;
import realworld.comments.model.CommentOrderBy;
import realworld.comments.services.CommentsService;

/**
 * Tests for the {@link CommentsServiceAuthorizerImpl}.
 */
@EnableAutoWeld
@AddEnabledDecorators(CommentsServiceAuthorizerImpl.class)
@AddBeanClasses({CommentsServiceAuthorizerImplTest.DummyCommentsService.class, CommentsServiceAuthorizerDependenciesProducer.class})
@ExtendWith(MockitoExtension.class)
public class CommentsServiceAuthorizerImplTest {

	public interface SpyCommentsService extends CommentsService {
		String getLastCall();
	}

	public static class DummyCommentsService implements SpyCommentsService {
		private String lastCall;

		@Override
		public String getLastCall() {
			return lastCall;
		}

		@Override
		public Comment createForCurrentUser(String slug, CommentCreationData comment) {
			return FROM_CREATE_FOR_CURRENT_USER;
		}

		@Override
		public SearchResult<Comment> findCommentsForArticle(String slug, Paging<CommentOrderBy> paging) {
			return FROM_FIND_COMMENTS_FOR_ARTICLE;
		}

		@Override
		public void delete(String id) {
			lastCall = LAST_CALL_DELETE;
		}

		@Override
		public void deleteAllForArticle(String articleId) {
			lastCall = LAST_CALL_DELETE_ALL_FOR_ARTICLE;
		}
	}

	private static final String ARTICLE_ID = "a971c13-1d";
	private static final String ARTICLE_SLUG = "slug";
	private static final String COMMENT_ID = "c066347-1d";
	private static final String BODY = "Some body text";
	private static final Comment FROM_CREATE_FOR_CURRENT_USER = mock(Comment.class);
	private static final SearchResult<Comment> FROM_FIND_COMMENTS_FOR_ARTICLE = new SearchResult<>();
	private static final String LAST_CALL_DELETE = "LAST_CALL_DELETE";
	private static final String LAST_CALL_DELETE_ALL_FOR_ARTICLE = "LAST_CALL_DELETE_ALL_FOR_ARTICLE";

	@Inject
	private CommentsServiceAuthorizerDependenciesProducer dependenciesProducer;

	@Inject
	private SpyCommentsService sut;

	@Test
	void testCreateForCurrentUserWithoutLogin() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireLogin();
		CommentCreationData commentCreationData = mock(CommentCreationData.class);
		expectNotAuthenticatedException(() -> sut.createForCurrentUser(ARTICLE_SLUG, commentCreationData));
	}

	@Test
	void testCreateForCurrentUserWithLogin() {
		doNothing().when(dependenciesProducer.getAuthorization()).requireLogin();
		CommentCreationData commentCreationData = mock(CommentCreationData.class);
		var result = sut.createForCurrentUser(ARTICLE_SLUG, commentCreationData);
		assertSame(FROM_CREATE_FOR_CURRENT_USER, result);
	}

	@Test
	void testFindCommentsForArticle() {
		Paging<CommentOrderBy> paging = new Paging<>();
		var result = sut.findCommentsForArticle(ARTICLE_SLUG, paging);
		assertSame(FROM_FIND_COMMENTS_FOR_ARTICLE, result);
		verifyNoInteractions(dependenciesProducer.getAuthorization());
	}

	@Test
	void testDeleteNotAuth() {
		doThrow(NotAuthorizedException.class).when(dependenciesProducer.getCommentsAuthorization()).authorizeDelete(COMMENT_ID);
		expectNotAuthorizedException(() -> sut.delete(COMMENT_ID));
	}

	@Test
	void testDelete() {
		doNothing().when(dependenciesProducer.getCommentsAuthorization()).authorizeDelete(COMMENT_ID);
		sut.delete(COMMENT_ID);
		assertEquals(LAST_CALL_DELETE, sut.getLastCall());
	}

	@Test
	void testDeleteAllForArticleNotAuth() {
		doThrow(NotAuthorizedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		expectNotAuthorizedException(() -> sut.deleteAllForArticle(ARTICLE_ID));
	}

	@Test
	void testDeleteAllForArticle() {
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		sut.deleteAllForArticle(ARTICLE_ID);
		assertEquals(LAST_CALL_DELETE_ALL_FOR_ARTICLE, sut.getLastCall());
	}
}
