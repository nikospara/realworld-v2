package realworld.article.services.v1;

import java.util.Optional;

import realworld.SearchResult;
import realworld.article.model.v1.Article;
import realworld.article.model.v1.ArticleSearchCriteria;
import realworld.article.model.v1.ArticleSearchResult;
import realworld.article.model.v1.ArticleUpsertData;
import realworld.article.model.v1.AuthenticatedUser;
import realworld.article.model.v1.Author;
import realworld.article.model.v1.User;

/**
 * Domain services interface for the article domain.
 */
public interface ArticleService {
	/**
	 * Search for articles.
	 *
	 * @param connectedUser   The connected user to the system that is running this method, can be null/not authenticated
	 * @param userWhoSearches The user on behalf of which to execute the search - this can be the unauthenticated user
	 *                        and is used to calculate the favorited flag and the "following author" flag of the results
	 * @param criteria        The search criteria
	 * @return The results
	 */
	SearchResult<ArticleSearchResult> search(AuthenticatedUser connectedUser, User userWhoSearches, ArticleSearchCriteria criteria);

	/**
	 * Fetch article by slug exact match.
	 *
	 * @param connectedUser   The connected user to the system that is running this method, can be null/not authenticated
	 * @param userWhoSearches The user on behalf of which to execute - this can be the unauthenticated user and
	 *                        is used to calculate the favorited flag and the "following author" flag of the result
	 * @param slug            The slug to search
	 * @return The article, if found
	 */
	Optional<ArticleSearchResult> fetchBySlug(AuthenticatedUser connectedUser, User userWhoSearches, String slug);

	Article create(AuthenticatedUser connectedUser, Author author, ArticleUpsertData data);

	// TODO Maybe move to Article? What are the pros and cons?
	Article update(AuthenticatedUser connectedUser, Author author, ArticleUpsertData data);

	// TODO Maybe move to Article? What are the pros and cons?
	void delete(AuthenticatedUser connectedUser, String slug);
}
