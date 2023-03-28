package realworld.article.dao;

import java.time.LocalDateTime;
import java.util.Set;

import realworld.EntityDoesNotExistException;
import realworld.SearchResult;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.article.model.v1.ArticleUpsertData;
import realworld.model.common.v1.AuthorId;

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
	 * @param slug         The slug to use
	 * @param creationDate The date to set as creation date
	 * @return The id of the new article
	 */
	String create(ArticleCreationData creationData, String slug, LocalDateTime creationDate);

	/**
	 * Create a new article (and related entities).
	 *
	 * @param data         The article creation data
	 * @param slug         The slug to use
	 * @param authorId     The author id to set
	 * @param creationDate The date to set as creation date
	 * @return The id of the new article
	 */
	String create(ArticleUpsertData data, String slug, AuthorId authorId, LocalDateTime creationDate);

	/**
	 * Update an article (and related entities).
	 *
	 * @param slug       The slug to use
	 * @param updateData The article update data
	 * @param updateTime The update time to set
	 * @return The id of the updated article
	 */
	String update(String slug, ArticleUpdateData updateData, LocalDateTime updateTime);

	/**
	 * Delete an article.
	 *
	 * @param slug The slug of the article to delete
	 */
	void delete(String slug);

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

	/**
	 * Search for articles.
	 *
	 * @param userId   The current user id, to calculate {@code favorited}
	 * @param criteria The search criteria
	 * @return The results
	 */
	SearchResult<ArticleSearchResult> find(String userId, ArticleSearchCriteria criteria);
}
