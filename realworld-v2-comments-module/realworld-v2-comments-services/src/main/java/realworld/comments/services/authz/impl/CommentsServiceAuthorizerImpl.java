package realworld.comments.services.authz.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.Paging;
import realworld.SearchResult;
import realworld.authorization.service.Authorization;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentCreationData;
import realworld.comments.model.CommentOrderBy;
import realworld.comments.services.CommentsService;
import realworld.comments.services.authz.CommentsAuthorization;

/**
 * Security for the {@link CommentsService}.
 */
@Decorator
@Priority(APPLICATION)
class CommentsServiceAuthorizerImpl implements CommentsService {

	private CommentsService delegate;

	private Authorization authorization;

	private CommentsAuthorization commentsAuthorization;

	/**
	 * Constructor for frameworks.
	 */
	public CommentsServiceAuthorizerImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param delegate The delegate of this decorator
	 * @param authorization          The general authorization
	 * @param commentsAuthorization  The comments-specific authorization logic
	 */
	@Inject
	public CommentsServiceAuthorizerImpl(@Delegate CommentsService delegate, Authorization authorization, CommentsAuthorization commentsAuthorization) {
		this.delegate = delegate;
		this.authorization = authorization;
		this.commentsAuthorization = commentsAuthorization;
	}

	@Override
	public Comment createForCurrentUser(String slug, CommentCreationData comment) {
		authorization.requireLogin();
		return delegate.createForCurrentUser(slug, comment);
	}

	@Override
	public SearchResult<Comment> findCommentsForArticle(String slug, Paging<CommentOrderBy> paging) {
		return delegate.findCommentsForArticle(slug, paging);
	}

	@Override
	public void delete(String id) {
		commentsAuthorization.authorizeDelete(id);
		delegate.delete(id);
	}

	@Override
	public void deleteAllForArticle(String articleId) {
		authorization.requireSystemUser();
		delegate.deleteAllForArticle(articleId);
	}
}
