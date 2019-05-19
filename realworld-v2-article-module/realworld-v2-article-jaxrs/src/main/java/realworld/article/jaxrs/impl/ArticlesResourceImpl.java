package realworld.article.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import realworld.article.jaxrs.ArticleCombinedFullDataDto;
import realworld.article.jaxrs.ArticlesResource;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.services.ArticleService;

/**
 * Implementation of the {@link ArticlesResource}.
 */
@RequestScoped
public class ArticlesResourceImpl implements ArticlesResource {

	@Inject
	private ArticleService articleService;

	@Override
	public ArticleCombinedFullDataDto get(String slug) {
		ArticleCombinedFullData data = articleService.findFullDataBySlug(slug);
		ArticleCombinedFullDataDto result = new ArticleCombinedFullDataDto();
		result.setArticle(data.getArticle());
//		result.setAuthor(XXX);
		result.setBody(data.getBody());
		result.setFavorited(data.isFavorited());
		result.setFavoritesCount(data.getFavoritesCount());
		return result;
	}
}
