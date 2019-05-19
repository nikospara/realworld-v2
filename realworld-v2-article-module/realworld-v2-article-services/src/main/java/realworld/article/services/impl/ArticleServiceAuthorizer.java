package realworld.article.services.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.article.model.ArticleCombinedFullData;
import realworld.article.services.ArticleService;

/**
 * Security for the {@link ArticleService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class ArticleServiceAuthorizer implements ArticleService {

	private ArticleService delegate;

	@Inject
	public ArticleServiceAuthorizer(@Delegate ArticleService delegate) {
		this.delegate = delegate;
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug) {
		return delegate.findFullDataBySlug(slug);
	}
}
