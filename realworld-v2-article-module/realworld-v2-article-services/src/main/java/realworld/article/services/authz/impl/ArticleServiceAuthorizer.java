package realworld.article.services.authz.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.SearchResult;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.article.services.ArticleService;
import realworld.article.services.authz.ArticleAuthorization;
import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.article.services.ArticleService} implementation.
 */
@Decorator
@Priority(APPLICATION)
class ArticleServiceAuthorizer implements ArticleService {

	private ArticleService delegate;

	private Authorization authorization;

	private ArticleAuthorization articleAuthorization;

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	ArticleServiceAuthorizer() {
		// NOOP
	}

	/**
	 * Constructor for injection.
	 *
	 * @param delegate The delegate of this decorator
	 * @param authorization The authorization
	 * @param articleAuthorization The article authorization logic
	 */
	@Inject
	public ArticleServiceAuthorizer(@Delegate ArticleService delegate, Authorization authorization, ArticleAuthorization articleAuthorization) {
		this.delegate = delegate;
		this.authorization = authorization;
		this.articleAuthorization = articleAuthorization;
	}

	@Override
	public ArticleBase create(ArticleCreationData creationData) {
		authorization.requireUserId(creationData.getAuthorId());
		return delegate.create(creationData);
	}

	@Override
	public String update(String slug, ArticleUpdateData updateData) {
		articleAuthorization.authorizeUpdate(slug, updateData);
		return delegate.update(slug, updateData);
	}

	@Override
	public void delete(String slug) {
		articleAuthorization.authorizeDelete(slug);
		delegate.delete(slug);
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug) {
		return delegate.findFullDataBySlug(slug);
	}

	@Override
	public SearchResult<ArticleSearchResult> find(ArticleSearchCriteria criteria) {
		return delegate.find(criteria);
	}
}
