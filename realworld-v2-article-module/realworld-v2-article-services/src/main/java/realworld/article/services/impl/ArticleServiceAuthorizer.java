package realworld.article.services.impl;

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

/**
 * Security for the {@link realworld.article.services.ArticleService}.
 */
public interface ArticleServiceAuthorizer {
	/**
	 * Authorization logic for {@link realworld.article.services.ArticleService#create(ArticleCreationData)}.
	 *
	 * @param creationData Input
	 * @param delegate     Delegate
	 * @return The return value of the delegate
	 */
	ArticleBase create(ArticleCreationData creationData, Function<ArticleCreationData,ArticleBase> delegate);

	/**
	 * Authorization logic for {@link realworld.article.services.ArticleService#update(String,ArticleUpdateData)}.
	 *
	 * @param slug       The article slug
	 * @param updateData The article update data
	 * @param delegate   The delegate
	 * @return The return value of the delegate
	 */
	String update(String slug, ArticleUpdateData updateData, BiFunction<String, ArticleUpdateData, String> delegate);

	/**
	 * Authorization logic for {@link realworld.article.services.ArticleService#delete(String)}.
	 *
	 * @param slug       The article slug
	 * @param delegate   The delegate
	 */
	void delete(String slug, Consumer<String> delegate);

	/**
	 * Authorization logic for {@link realworld.article.services.ArticleService#findFullDataBySlug(String)}.
	 *
	 * @param slug     Input
	 * @param delegate Delegate
	 * @return The return value of the delegate
	 */
	ArticleCombinedFullData findFullDataBySlug(String slug, Function<String,ArticleCombinedFullData> delegate);

	/**
	 * Authorization logic for {@link realworld.article.services.ArticleService#find(ArticleSearchCriteria)}.
	 *
	 * @param criteria Input
	 * @param delegate Delegate
	 * @return The return value of the delegate
	 */
	SearchResult<ArticleSearchResult> find(ArticleSearchCriteria criteria, Function<ArticleSearchCriteria,SearchResult<ArticleSearchResult>> delegate);
}
