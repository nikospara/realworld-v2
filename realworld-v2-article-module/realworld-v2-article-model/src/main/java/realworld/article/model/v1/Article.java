package realworld.article.model.v1;

import java.time.LocalDateTime;
import java.util.Set;

import realworld.model.common.v1.ArticleId;
import realworld.model.common.v1.AuthorId;
import realworld.model.common.v1.FormattedText;
import realworld.model.common.v1.StructuredText;


/**
 * An article is a central domain entity for this application.
 */
public interface Article {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	ArticleId getId();

	/**
	 * The slug, i.e. a representation of the title that is suitable to be part of a URL.
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
	 * The author id of this article.
	 *
	 * @return The author id of this article
	 */
	AuthorId getAuthorId();

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
