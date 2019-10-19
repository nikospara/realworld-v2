package realworld.article.services.impl;

import java.util.function.Function;

import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;

/**
 * Security for the {@link realworld.article.services.ArticleService}.
 */
public interface ArticleServiceAuthorizer {
	ArticleBase create(ArticleCreationData creationData, Function<ArticleCreationData,ArticleBase> delegate);

	ArticleCombinedFullData findFullDataBySlug(String slug, Function<String,ArticleCombinedFullData> delegate);
}
