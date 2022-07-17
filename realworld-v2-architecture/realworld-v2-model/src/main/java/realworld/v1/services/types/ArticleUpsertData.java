package realworld.v1.services.types;

import java.util.Set;

import realworld.v1.types.ArticleId;
import realworld.v1.types.FormattedText;
import realworld.v1.types.StructuredText;
import realworld.v1.model.ArticleBody;
import realworld.v1.model.Tag;

public interface ArticleUpsertData {
	/**
	 * The id.
	 *
	 * @return The id.
	 */
	ArticleId getId();

	/**
	 * The title.
	 *
	 * @return The title
	 */
	FormattedText getTitle();

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
}
