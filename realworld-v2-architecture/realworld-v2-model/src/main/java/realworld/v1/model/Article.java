package realworld.v1.model;

import java.time.LocalDateTime;
import java.util.Set;

import realworld.v1.types.ArticleId;
import realworld.v1.types.FormattedText;
import realworld.v1.types.StructuredText;

/**
 * An article is the central entity for this application.
 */
public interface Article {
	/**
	 * The id.
	 *
	 * @return The id.
	 */
	ArticleId getId();

	/**
	 * The slug.
	 *
	 * @return The slug
	 */
	String getSlug();

	/**
	 * The title.
	 *
	 * @return The title
	 */
	FormattedText getTitle();

	/**
	 * The author of this article.
	 *
	 * @return The author of this article
	 */
	Author getAuthor();

	/**
	 * The description.
	 *
	 * @return The description
	 */
	StructuredText getDescription();

	/**
	 * The article body.
	 *
	 * @return The article body
	 */
	ArticleBody getArticleBody();

	/**
	 * Get the tags for this article.
	 *
	 * @return The tags for this article
	 */
	Set<Tag> getTags();

	/**
	 * The favorites count.
	 *
	 * @return The favorites count
	 */
	int getFavoritesCount();

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
