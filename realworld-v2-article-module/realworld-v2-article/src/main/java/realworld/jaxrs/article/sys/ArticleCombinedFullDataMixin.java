package realworld.jaxrs.article.sys;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import realworld.article.model.ArticleBase;

/**
 * Mixin for the {@link realworld.article.model.ArticleCombinedFullData}.
 */
class ArticleCombinedFullDataMixin {
	@JsonUnwrapped
	private ArticleBase article;
}
