package realworld.article.model;

import org.immutables.value.Value;

/**
 * Article that has links to its related entities, i.e. contains only their ids.
 */
@Value.Immutable
public interface ArticleWithLinks {

	ArticleBase getArticle();

	/**
	 * The author id.
	 *
	 * @return The author id
	 */
	String getAuthorId();
}
