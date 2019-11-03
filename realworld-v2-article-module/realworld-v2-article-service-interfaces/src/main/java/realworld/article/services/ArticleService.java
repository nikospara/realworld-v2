package realworld.article.services;

import realworld.EntityDoesNotExistException;
import realworld.SearchResult;
import realworld.SimpleValidationException;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;

/**
 * Article services.
 */
public interface ArticleService {

	/**
	 * Create an article.
	 *
	 * @param creationData The article creation data
	 */
	ArticleBase create(ArticleCreationData creationData);

	/**
	 * Retrieve the full article data by slug.
	 *
	 * @param slug The slug
	 * @return The full article data
	 * @throws EntityDoesNotExistException If an article with the given slug does not exist
	 * @throws SimpleValidationException If the slug is duplicate
	 */
	ArticleCombinedFullData findFullDataBySlug(String slug);

	/**
	 * Search for articles.
	 *
	 * @param criteria The criteria
	 * @return The results
	 */
	SearchResult<ArticleSearchResult> find(ArticleSearchCriteria criteria);
}
