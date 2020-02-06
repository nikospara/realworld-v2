package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Objects;
import java.util.Optional;

import realworld.EntityDoesNotExistException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleUpdateData;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.authorization.NotAuthorizedException;
import realworld.authorization.service.Authorization;

/**
 * Implementation of the {@link ArticleAuthorization}.
 */
@ApplicationScoped
class ArticleAuthorizationImpl implements ArticleAuthorization {

	private AuthenticationContext authenticationContext;

	private ArticleDao articleDao;

	private Authorization authorization;

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	ArticleAuthorizationImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param authenticationContext The authentication context
	 * @param articleDao            The article DAO
	 * @param authorization         The general authorization service
	 */
	@Inject
	public ArticleAuthorizationImpl(AuthenticationContext authenticationContext, ArticleDao articleDao, Authorization authorization) {
		this.authenticationContext = authenticationContext;
		this.articleDao = articleDao;
		this.authorization = authorization;
	}

	@Override
	public void requireCurrentUserToBeAuthorOf(String slug) {
		try {
			authorization.requireLogin();
			// TODO Implement caching to share this article with the business code
			ArticleCombinedFullData article = articleDao.findFullDataBySlug(authenticationContext.getUserPrincipal().getUniqueId(), slug);
			requireCurrentUserToBeAuthorOf(article);
		}
		catch( EntityDoesNotExistException edne ) {
			// ignore
		}
	}

	/**
	 * Implement article update authorization checks.
	 * <p>
	 * If any of the protected fields of the article (such as {@code creationDate})
	 * have changed, requires system user.
	 * If a protected field is defined in the update data, but the value is the same as
	 * the original, this method treats it as unchanged - i.e. no special user required.
	 * <p>
	 * If not protected fields have changed, require that the current user is authenticated
	 * and has authored the article.
	 * <p>
	 * Returns silently in any case, if the article does not exist.
	 *
	 * @param slug       The slug to identify the article
	 * @param updateData The update data
	 */
	@Override
	public void authorizeUpdate(String slug, ArticleUpdateData updateData) {
		if( updateData == null ) {
			return;
		}
		try {
			// TODO Implement caching to share this article with the business code
			ArticleCombinedFullData article = articleDao.findFullDataBySlug(Optional.ofNullable(authenticationContext.getUserPrincipal()).map(User::getUniqueId).orElse(null), slug);
			requireSystemUserIfDifferent(updateData.getAuthorId(), article.getAuthor().getId());
			requireSystemUserIfDifferent(updateData.getCreatedAt(), article.getArticle().getCreatedAt());
			requireSystemUserIfDifferent(updateData.getUpdatedAt(), article.getArticle().getUpdatedAt());
			if( !authenticationContext.isSystem() ) {
				authorization.requireLogin();
				requireCurrentUserToBeAuthorOf(article);
			}
		}
		catch( EntityDoesNotExistException edne ) {
			// ignore
		}
	}

	private void requireCurrentUserToBeAuthorOf(ArticleCombinedFullData article) {
		if( !article.getAuthor().getId().equals(authenticationContext.getUserPrincipal().getUniqueId()) ) {
			throw new NotAuthorizedException();
		}
	}

	private void requireSystemUserIfDifferent(Optional<?> a, Object b) {
		if( a != null && !Objects.equals(a.orElse(null), b) ) {
			authorization.requireSystemUser();
		}
	}
}
