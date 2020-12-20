package realworld.comments.services.impl;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import realworld.Paging;
import realworld.SearchResult;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;

/**
 * Security for the {@link realworld.comments.services.CommentsService}.
 */
public interface CommentsServiceAuthorizer {
	/**
	 * Authorization logic for {@link realworld.comments.services.CommentsService#createForCurrentUser(String, String)}.
	 *
	 * @param articleId Passthrough input
	 * @param body      Passthrough input
	 * @param delegate  The delegate
	 * @return The return value of the delegate
	 */
	Comment createForCurrentUser(String articleId, String body, BiFunction<String, String, Comment> delegate);

	/**
	 * Authorization logic for {@link realworld.comments.services.CommentsService#findCommentsForArticle(String, Paging)}.
	 *
	 * @param articleId Passthrough input
	 * @param paging    Passthrough input
	 * @param delegate  The delegate
	 * @return The return value of the delegate
	 */
	SearchResult<Comment> findCommentsForArticle(String articleId, Paging<CommentOrderBy> paging, BiFunction<String,Paging<CommentOrderBy>, SearchResult<Comment>> delegate);

	/**
	 * Authorization logic for {@link realworld.comments.services.CommentsService#delete(String)}.
	 *
	 * @param id        Passthrough input
	 * @param delegate  The delegate
	 */
	void delete(String id, Consumer<String> delegate);

	/**
	 * Authorization logic for {@link realworld.comments.services.CommentsService#deleteAllForArticle(String)}.
	 *
	 * @param articleId Passthrough input
	 * @param delegate  The delegate
	 */
	void deleteAllForArticle(String articleId, Consumer<String> delegate);
}
