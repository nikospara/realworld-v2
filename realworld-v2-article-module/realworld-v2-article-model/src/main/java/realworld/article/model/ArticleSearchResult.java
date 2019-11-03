package realworld.article.model;

import java.util.Set;

import org.immutables.value.Value;
import realworld.article.model.ArticleBase;

/**
 * Result from article search.
 */
@Value.Immutable
public interface ArticleSearchResult {

	/**
	 * Get the main article data.
	 *
	 * @return The main article data
	 */
	ArticleBase getArticle();

	/**
	 * Get the tags for this article.
	 *
	 * @return The tags for this article
	 */
	Set<String> getTagList();

	boolean isFavorited();

	int getFavoritesCount();

	/**
	 * Get the author id.
	 *
	 * @return The author id
	 */
	String getAuthorId();
}
