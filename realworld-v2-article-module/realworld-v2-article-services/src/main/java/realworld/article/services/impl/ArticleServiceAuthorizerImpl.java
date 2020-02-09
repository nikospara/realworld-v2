package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import realworld.SearchResult;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.article.services.ArticleService} implementation.
 */
@ApplicationScoped
class ArticleServiceAuthorizerImpl implements ArticleServiceAuthorizer {

	private Authorization authorization;

	private ArticleAuthorization articleAuthorization;

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
	 * @param articleAuthorization The article authorization logic
	 */
	@Inject
	public ArticleServiceAuthorizerImpl(Authorization authorization, ArticleAuthorization articleAuthorization) {
		this.authorization = authorization;
		this.articleAuthorization = articleAuthorization;
	}

	@Override
	public ArticleBase create(ArticleCreationData creationData, Function<ArticleCreationData, ArticleBase> delegate) {
		authorization.requireUserId(creationData.getAuthorId());
		return delegate.apply(creationData);
	}

	@Override
	public String update(String slug, ArticleUpdateData updateData, BiFunction<String, ArticleUpdateData, String> delegate) {
		articleAuthorization.authorizeUpdate(slug, updateData);
		return delegate.apply(slug, updateData);
	}

	@Override
	public void delete(String slug, Consumer<String> delegate) {
		articleAuthorization.authorizeDelete(slug);
		delegate.accept(slug);
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug, Function<String, ArticleCombinedFullData> delegate) {
		return delegate.apply(slug);
	}

	@Override
	public SearchResult<ArticleSearchResult> find(ArticleSearchCriteria criteria, Function<ArticleSearchCriteria, SearchResult<ArticleSearchResult>> delegate) {
		return delegate.apply(criteria);
	}
}
