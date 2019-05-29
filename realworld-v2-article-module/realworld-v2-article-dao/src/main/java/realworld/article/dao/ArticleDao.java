package realworld.article.dao;

import java.time.LocalDateTime;
import java.util.Set;

import realworld.EntityDoesNotExistException;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;

/**
 * DAO interface for the Article entity.
 */
public interface ArticleDao {

	/**
	 * Check if an article with the given slug exists.
	 *
	 * @param slug The slug
	 * @return Whether an article exists
	 */
	boolean slugExists(String slug);

	/**
	 * Create a new article (and related entities).
	 *
	 * @param creationData The article creation data
	 * @param creationDate The date to set as creation date
	 * @param slug         The slug to use
	 * @return The id of the new article
	 */
	String create(ArticleCreationData creationData, String slug, LocalDateTime creationDate);

	/**
	 * Retrieve the full article data by slug.
	 *
	 * @param userId The user id to calculate {@code ArticleCombinedFullData.favorited}
	 * @param slug The slug
	 * @return The full article data
	 * @throws EntityDoesNotExistException If an article with the given slug does not exist
	 */
	ArticleCombinedFullData findFullDataBySlug(String userId, String slug);

	/**
	 * Find the tags of the given article.
	 *
	 * @param articleId The article id
	 * @return The set of tags
	 * @throws EntityDoesNotExistException If the article does not exist
	 */
	Set<String> findTags(String articleId);

	/**
	 * Find an article id by slug.
	 *
	 * @param slug The slug to search
	 * @return The article id
	 * @throws EntityDoesNotExistException If the article does not exist
	 */
	String findArticleIdBySlug(String slug);
}
