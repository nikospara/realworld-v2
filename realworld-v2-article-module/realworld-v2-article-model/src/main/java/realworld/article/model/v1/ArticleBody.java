package realworld.article.model.v1;

import realworld.model.common.v1.ArticleId;
import realworld.model.common.v1.StructuredText;

/**
 * Representation of the body text of an article.
 */
public interface ArticleBody {
	/**
	 * The id of the article containing this body text.
	 * Since the article has one body, this is effectively the id of the body text too.
	 *
	 * @return The id of the article containing this body text
	 */
	ArticleId getId();

	StructuredText getBody();
}
