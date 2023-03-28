package realworld.article.model.v1;

import java.util.Set;

import realworld.article.model.v1.ArticleBody;
import realworld.article.model.v1.Tag;
import realworld.model.common.v1.ArticleId;
import realworld.model.common.v1.FormattedText;
import realworld.model.common.v1.StructuredText;

/**
 * Information needed to update or insert an article.
 */
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
