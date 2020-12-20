package realworld.comments.services;

import realworld.Paging;
import realworld.SearchResult;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;

/**
 * Comments services.
 */
public interface CommentsService {
	/**
	 * Create a comment on behalf of the current with the default data.
	 *
	 * @param articleId The article this comment is about
	 * @param body      The body of the comment
	 * @return The created {@code Comment} object
	 */
	Comment createForCurrentUser(String articleId, String body);

	/**
	 * Find the comments for an article, optionally sorted and paged.
	 *
	 * @param articleId The article for which to fetch the comments
	 * @param paging    Paging instructions (if {@code null}, the default is fetch all, sort by creation date descending)
	 * @return The search results
	 */
	SearchResult<Comment> findCommentsForArticle(String articleId, Paging<CommentOrderBy> paging);

	/**
	 * Delete the comment with the specified id.
	 *
	 * @param id Id of comment to delete
	 */
	void delete(String id);

	/**
	 * Delete all comments for the given article.
	 *
	 * @param articleId The article id
	 */
	void deleteAllForArticle(String articleId);
}
