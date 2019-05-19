package realworld.article.services;

import realworld.EntityDoesNotExistException;
import realworld.article.model.ArticleCombinedFullData;

/**
 * Article services.
 */
public interface ArticleService {

	/**
	 * Retrieve the full article data by slug.
	 *
	 * @param slug The slug
	 * @return The full article data
	 * @throws EntityDoesNotExistException If an article with the given slug does not exist
	 */
	ArticleCombinedFullData findFullDataBySlug(String slug);
}
