package realworld.comments.dao;

import java.util.List;

import realworld.Paging;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;

/**
 * DAO interface for the Comment entity.
 */
public interface CommentsDao {
	/**
	 * Create a new comment.
	 *
	 * @param comment The comment to create
	 * @return The new id
	 */
	String create(Comment comment);

	/**
	 * Find a comment by id.
	 *
	 * @param id The comment id
	 * @return The comment model object, or {@code null} if it doesn't exist
	 */
	Comment findById(String id);

	/**
	 * Delete the comment with the given id.
	 *
	 * @param id The comment id
	 */
	void delete(String id);

	/**
	 * Delete all comments for the given article.
	 *
	 * @param articleId The article id
	 */
	void deleteAllForArticle(String articleId);

	/**
	 * Count comments for the given article.
	 *
	 * @param articleId The article id
	 * @return The count of comments
	 */
	long countCommentsForArticle(String articleId);

	/**
	 * Find the comments for an article, optionally sorted and paged.
	 *
	 * @param slug      The slug of the article for which to fetch the comments
	 * @param paging    Paging instructions (if {@code null}, the default is fetch all, sort by creation date descending)
	 * @return The page of results
	 */
	List<Comment> findCommentsForArticlePaged(String slug, Paging<CommentOrderBy> paging);
}
