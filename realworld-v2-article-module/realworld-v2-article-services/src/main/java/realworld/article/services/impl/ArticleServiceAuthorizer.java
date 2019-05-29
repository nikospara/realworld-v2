package realworld.article.services.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.services.ArticleService;
import realworld.authorization.service.Authorization;

/**
 * Security for the {@link ArticleService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class ArticleServiceAuthorizer implements ArticleService {

	private ArticleService delegate;

	private Authorization authorization;

	@Inject
	public ArticleServiceAuthorizer(@Delegate ArticleService delegate, Authorization authorization) {
		this.delegate = delegate;
		this.authorization = authorization;
	}

	@Override
	public ArticleBase create(ArticleCreationData creationData) {
		authorization.requireUserId(creationData.getAuthorId());
		return delegate.create(creationData);
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug) {
		return delegate.findFullDataBySlug(slug);
	}
}
