package realworld.article.jaxrs.impl;

import javax.enterprise.context.RequestScoped;

import java.time.LocalDateTime;

import realworld.article.jaxrs.ArticlesResource;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ImmutableArticleBase;

/**
 * Implementation of the {@link ArticlesResource}.
 */
@RequestScoped
public class ArticlesResourceImpl implements ArticlesResource {
	@Override
	public ArticleCombinedFullData get(String slug) {
		ArticleCombinedFullData result = new ArticleCombinedFullData();
		result.setArticle(ImmutableArticleBase.builder().createdAt(LocalDateTime.now()).description("").id("id").slug(slug).title("title").updatedAt(LocalDateTime.now()).build());
		result.setBody("body text");
		return result;
	}
}
