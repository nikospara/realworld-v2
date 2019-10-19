package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.article.services.ArticleService} implementation.
 */
@ApplicationScoped
public class ArticleServiceAuthorizerImpl implements ArticleServiceAuthorizer {

	private Authorization authorization;

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	ArticleServiceAuthorizerImpl() {
		// NOOP
	}

	/**
	 * Constructor for injection.
	 *
	 * @param authorization The authorization
	 */
	@Inject
	public ArticleServiceAuthorizerImpl(Authorization authorization) {
		this.authorization = authorization;
	}

	@Override
	public ArticleBase create(ArticleCreationData creationData, Function<ArticleCreationData, ArticleBase> delegate) {
		authorization.requireUserId(creationData.getAuthorId());
		return delegate.apply(creationData);
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug, Function<String, ArticleCombinedFullData> delegate) {
		return delegate.apply(slug);
	}
}