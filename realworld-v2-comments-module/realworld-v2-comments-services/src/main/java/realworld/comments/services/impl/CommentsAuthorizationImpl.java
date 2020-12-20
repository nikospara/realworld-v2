package realworld.comments.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import realworld.authentication.AuthenticationContext;
import realworld.authorization.NotAuthorizedException;
import realworld.authorization.service.Authorization;
import realworld.comments.dao.CommentsDao;
import realworld.comments.model.Comment;

/**
 * Implementation of the {@link CommentsAuthorization}.
 */
@ApplicationScoped
class CommentsAuthorizationImpl implements CommentsAuthorization {

	private AuthenticationContext authenticationContext;

	private Authorization authorization;

	private CommentsDao dao;

	/**
	 * Constructor for frameworks.
	 */
	public CommentsAuthorizationImpl() {
		// NOOP
	}

	/**
	 * Constructor for injection.
	 *
	 * @param authenticationContext The authentication context
	 * @param authorization         The generic authorization logic
	 * @param dao                   The comments DAO
	 */
	@Inject
	public CommentsAuthorizationImpl(AuthenticationContext authenticationContext, Authorization authorization, CommentsDao dao) {
		this.authenticationContext = authenticationContext;
		this.authorization = authorization;
		this.dao = dao;
	}

	@Override
	public void authorizeDelete(String id) {
		if( !authenticationContext.isSystem() ) {
			requireCurrentUserToBeAuthorOf(id);
		}
	}

	private void requireCurrentUserToBeAuthorOf(String id) {
		authorization.requireLogin();
		// TODO Maybe implement caching to share this comment with the business code
		Comment comment = dao.findById(id);
		requireCurrentUserToBeAuthorOf(comment);
	}

	private void requireCurrentUserToBeAuthorOf(Comment comment) {
		if( comment != null && !comment.getAuthorId().equals(authenticationContext.getUserPrincipal().getUniqueId()) ) {
			throw new NotAuthorizedException();
		}
	}
}
