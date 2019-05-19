package realworld.article.dao;

import realworld.EntityDoesNotExistException;
import realworld.article.model.ArticleCombinedFullData;

/**
 * DAO interface for the Article entity.
 */
public interface ArticleDao {

	/**
	 * Retrieve the full article data by slug.
	 *
	 * @param userId The user id to calculate {@code ArticleCombinedFullData.favorited}
	 * @param slug The slug
	 * @return The full article data
	 * @throws EntityDoesNotExistException If an article with the given slug does not exist
	 */
	ArticleCombinedFullData findFullDataBySlug(String userId, String slug);
}
