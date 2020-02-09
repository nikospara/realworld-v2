package realworld.article.services.impl;

import realworld.article.model.ArticleUpdateData;
import realworld.authorization.AppSecurityException;

/**
 * Article module-specific methods for authorizing the access to article services.
 */
public interface ArticleAuthorization {

	/**
	 * Require that the current user is authenticated and has authored the article with the given slug.
	 * Returns silently if the article does not exist.
	 *
	 * @param slug The slug
	 * @throws AppSecurityException If the check fails
	 */
	void requireCurrentUserToBeAuthorOf(String slug);

	/**
	 * Authorize an update of the article with the given slug.
	 *
	 * @param slug       The slug to identify the article
	 * @param updateData The update data
	 */
	void authorizeUpdate(String slug, ArticleUpdateData updateData);

	/**
	 * Authorize the deletion of the article with the given slug.
	 *
	 * @param slug       The slug to identify the article
	 */
	void authorizeDelete(String slug);
}
