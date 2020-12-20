package realworld.comments.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthorizedException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.Paging;
import realworld.SearchResult;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.NotAuthorizedException;
import realworld.authorization.service.Authorization;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;

/**
 * Tests for the {@link CommentsServiceAuthorizerImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class CommentsServiceAuthorizerImplTest {

	private static final String ARTICLE_ID = "a971c13-1d";
	private static final String COMMENT_ID = "c066347-1d";
	private static final String BODY = "Some body text";

	@Produces @Mock
	private Authorization authorization;

	@Produces @Mock
	private CommentsAuthorization commentsAuthorization;

	@Inject
	private CommentsServiceAuthorizerImpl sut;

	@Test
	void testCreateForCurrentUserWithoutLogin() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireLogin();
		@SuppressWarnings("unchecked")
		BiFunction<String, String, Comment> mockDelegate = mock(BiFunction.class);
		expectNotAuthenticatedException(() -> sut.createForCurrentUser(ARTICLE_ID, BODY, mockDelegate));
	}

	@Test
	void testCreateForCurrentUserWithLogin() {
		doNothing().when(authorization).requireLogin();
		@SuppressWarnings("unchecked")
		BiFunction<String, String, Comment> mockDelegate = mock(BiFunction.class);
		Comment mockResult = mock(Comment.class);
		when(mockDelegate.apply(ARTICLE_ID, BODY)).thenReturn(mockResult);
		var result = sut.createForCurrentUser(ARTICLE_ID, BODY, mockDelegate);
		assertSame(mockResult, result);
	}

	@Test
	void testFindCommentsForArticle() {
		@SuppressWarnings("unchecked")
		BiFunction<String, Paging<CommentOrderBy>, SearchResult<Comment>> mockDelegate = mock(BiFunction.class);
		@SuppressWarnings("unchecked")
		SearchResult<Comment> mockResult = mock(SearchResult.class);
		Paging<CommentOrderBy> paging = new Paging<>();
		when(mockDelegate.apply(ARTICLE_ID, paging)).thenReturn(mockResult);
		var result = sut.findCommentsForArticle(ARTICLE_ID, paging, mockDelegate);
		assertSame(mockResult, result);
		verifyNoInteractions(authorization);
	}

	@Test
	void testDeleteNotAuth() {
		doThrow(NotAuthorizedException.class).when(commentsAuthorization).authorizeDelete(COMMENT_ID);
		@SuppressWarnings("unchecked")
		Consumer<String> mockDelegate = mock(Consumer.class);
		expectNotAuthorizedException(() -> sut.delete(COMMENT_ID, mockDelegate));
	}

	@Test
	void testDelete() {
		doNothing().when(commentsAuthorization).authorizeDelete(COMMENT_ID);
		@SuppressWarnings("unchecked")
		Consumer<String> mockDelegate = mock(Consumer.class);
		sut.delete(COMMENT_ID, mockDelegate);
		verify(mockDelegate).accept(COMMENT_ID);
		verifyNoMoreInteractions(authorization, commentsAuthorization);
	}

	@Test
	void testDeleteAllForArticleNotAuth() {
		doThrow(NotAuthorizedException.class).when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		Consumer<String> mockDelegate = mock(Consumer.class);
		expectNotAuthorizedException(() -> sut.deleteAllForArticle(ARTICLE_ID, mockDelegate));
	}

	@Test
	void testDeleteAllForArticle() {
		doNothing().when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		Consumer<String> mockDelegate = mock(Consumer.class);
		sut.deleteAllForArticle(ARTICLE_ID, mockDelegate);
		verify(mockDelegate).accept(ARTICLE_ID);
		verifyNoMoreInteractions(authorization, commentsAuthorization);
	}
}
