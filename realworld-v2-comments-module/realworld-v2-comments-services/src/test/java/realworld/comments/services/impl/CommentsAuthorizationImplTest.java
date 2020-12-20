package realworld.comments.services.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthorizedException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.UUID;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.service.Authorization;
import realworld.comments.dao.CommentsDao;
import realworld.comments.model.Comment;

/**
 * Tests for the {@link CommentsAuthorizationImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class CommentsAuthorizationImplTest {

	private static final String COMMENT_ID = UUID.randomUUID().toString();
	private static final String USER_ID = UUID.randomUUID().toString();

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Produces @Mock
	private Authorization authorization;

	@Produces @Mock
	private CommentsDao dao;

	@Inject
	private CommentsAuthorizationImpl sut;

	@Test
	void testSystemUserIsAllowedToDelete() {
		when(authenticationContext.isSystem()).thenReturn(true);
		sut.authorizeDelete(UUID.randomUUID().toString());
		verifyNoMoreInteractions(authenticationContext, authorization, dao);
	}

	@Test
	void testAuthorIsAllowedToDelete() {
		when(authenticationContext.isSystem()).thenReturn(false);
		doNothing().when(authorization).requireLogin();
		Comment mockComment = mock(Comment.class);
		when(mockComment.getAuthorId()).thenReturn(USER_ID);
		when(dao.findById(COMMENT_ID)).thenReturn(mockComment);
		User mockUser = mock(User.class);
		when(mockUser.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(mockUser);
		sut.authorizeDelete(COMMENT_ID);
		verifyNoMoreInteractions(authenticationContext, authorization, dao);
	}

	@Test
	void testNonSystemNonAuthorThrowsOnDelete() {
		when(authenticationContext.isSystem()).thenReturn(false);
		doNothing().when(authorization).requireLogin();
		Comment mockComment = mock(Comment.class);
		when(mockComment.getAuthorId()).thenReturn(UUID.randomUUID().toString());
		when(dao.findById(COMMENT_ID)).thenReturn(mockComment);
		User mockUser = mock(User.class);
		when(mockUser.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(mockUser);
		expectNotAuthorizedException(() -> sut.authorizeDelete(COMMENT_ID));
	}

	@Test
	void testAnonymousUserCannotDelete() {
		when(authenticationContext.isSystem()).thenReturn(false);
		doThrow(NotAuthenticatedException.class).when(authorization).requireLogin();
		expectNotAuthenticatedException(() -> sut.authorizeDelete(COMMENT_ID));
	}

	@Test
	void testDeleteNonExistingCommentAllowed() {
		when(authenticationContext.isSystem()).thenReturn(false);
		doNothing().when(authorization).requireLogin();
		when(dao.findById(COMMENT_ID)).thenReturn(null);
		sut.authorizeDelete(COMMENT_ID);
		verifyNoMoreInteractions(authenticationContext, authorization, dao);
	}
}
