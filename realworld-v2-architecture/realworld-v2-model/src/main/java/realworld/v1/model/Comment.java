package realworld.v1.model;

import java.time.LocalDateTime;

import realworld.v1.types.CommentId;

/**
 * A comment from a user for an article.
 */
public interface Comment {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	CommentId getId();

	/**
	 * The user that created this comment, who is not necessarily an author.
	 *
	 * @return The user that created this comment
	 */
	User getUser();

	/**
	 * The article this comment applies to.
	 *
	 * @return The article this comment applies to
	 */
	Article getArticle();

	/**
	 * The comment body text.
	 *
	 * @return The comment body
	 */
	CommentBody getBody();

	/**
	 * The creation date.
	 *
	 * @return the creation date
	 */
	LocalDateTime getCreatedAt();

	/**
	 * The last modification date.
	 *
	 * @return The last modification date
	 */
	LocalDateTime getUpdatedAt();
}
