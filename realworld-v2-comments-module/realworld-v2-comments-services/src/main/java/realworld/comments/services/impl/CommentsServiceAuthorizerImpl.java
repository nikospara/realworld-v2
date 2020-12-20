package realworld.comments.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import realworld.Paging;
import realworld.SearchResult;
import realworld.authorization.service.Authorization;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;

/**
 * Implementation of the {@link CommentsServiceAuthorizer}.
 */
@ApplicationScoped
class CommentsServiceAuthorizerImpl implements CommentsServiceAuthorizer {

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
	 * @param authorization          The general authorization
	 * @param commentsAuthorization  The comments-specific authorization logic
	 */
	@Inject
	public CommentsServiceAuthorizerImpl(Authorization authorization, CommentsAuthorization commentsAuthorization) {
		this.authorization = authorization;
		this.commentsAuthorization = commentsAuthorization;
	}

	@Override
	public Comment createForCurrentUser(String articleId, String body, BiFunction<String, String, Comment> delegate) {
		authorization.requireLogin();
		return delegate.apply(articleId, body);
	}

	@Override
	public SearchResult<Comment> findCommentsForArticle(String articleId, Paging<CommentOrderBy> paging, BiFunction<String, Paging<CommentOrderBy>, SearchResult<Comment>> delegate) {
		return delegate.apply(articleId, paging);
	}

	@Override
	public void delete(String id, Consumer<String> delegate) {
		commentsAuthorization.authorizeDelete(id);
		delegate.accept(id);
	}

	@Override
	public void deleteAllForArticle(String articleId, Consumer<String> delegate) {
		authorization.requireSystemUser();
		delegate.accept(articleId);
	}
}
