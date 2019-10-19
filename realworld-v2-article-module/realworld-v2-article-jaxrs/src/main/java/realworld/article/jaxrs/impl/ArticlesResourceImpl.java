package realworld.article.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import realworld.ResourceLink;
import realworld.article.jaxrs.ArticleCombinedFullDataDto;
import realworld.article.jaxrs.ArticleCreationParam;
import realworld.article.jaxrs.ArticlesResource;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.services.ArticleService;

/**
 * Implementation of the {@link ArticlesResource}.
 */
@RequestScoped
public class ArticlesResourceImpl implements ArticlesResource {

	@Inject
	ArticleService articleService;

	@Context
	UriInfo uriInfo;

	@Override
	public Response create(ArticleCreationParam creationParam) {
		ArticleBase a = articleService.create(creationParam);
		return Response.created(uriInfo.getRequestUriBuilder().path(ArticlesResource.class, "get").build(a.getSlug())).build();
	}

	@Override
	public ArticleCombinedFullDataDto get(String slug) {
		ArticleCombinedFullData data = articleService.findFullDataBySlug(slug);
		ArticleCombinedFullDataDto result = new ArticleCombinedFullDataDto();
		result.setArticle(data.getArticle());
		result.setAuthor(new ResourceLink("TODO", "TODO"));
		result.setBody(data.getBody());
		result.setFavorited(data.isFavorited());
		result.setFavoritesCount(data.getFavoritesCount());
		result.setTagList(data.getTagList());
		return result;
	}
}
