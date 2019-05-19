package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import realworld.EntityDoesNotExistException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.services.ArticleService;
import realworld.authentication.AuthenticationContext;

/**
 * Implementation of the
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
public class ArticleServiceImpl implements ArticleService {

	private ArticleDao articleDao;

	private AuthenticationContext authenticationContext;

	@Inject
	public ArticleServiceImpl(ArticleDao articleDao, AuthenticationContext authenticationContext) {
		this.articleDao = articleDao;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug) {
		ArticleCombinedFullData result = articleDao.findFullDataBySlug(authenticationContext.getUserPrincipal() != null ? authenticationContext.getUserPrincipal().getUniqueId() : null, slug);
		// TODO fill-in missing details: tagList
		return result;
	}
}
