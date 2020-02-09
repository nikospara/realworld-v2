package realworld.article.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.stream.Collectors;

import realworld.NameAndId;
import realworld.ResourceLink;
import realworld.SearchResult;
import realworld.article.jaxrs.ArticleCombinedFullDataDto;
import realworld.article.jaxrs.ArticleCreationParam;
import realworld.article.jaxrs.ArticleSearchResultDto;
import realworld.article.jaxrs.ArticleUpdateParam;
import realworld.article.jaxrs.ArticlesResource;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.services.ArticleService;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ImmutableArticleSearchCriteria;

/**
 * Implementation of the {@link ArticlesResource}.
 */
@RequestScoped
public class ArticlesResourceImpl implements ArticlesResource {

	@Inject
	ArticleRestLayerConfig config;

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
	public Response update(String slug, ArticleUpdateParam updateParam) {
		articleService.update(slug, updateParam);
		return Response.noContent().header(HttpHeaders.LOCATION, uriInfo.getRequestUriBuilder().path(ArticlesResource.class, "get").build(slug)).build();
	}

	@Override
	public Response delete(String slug) {
		articleService.delete(slug);
		return Response.noContent().build();
	}

	@Override
	public ArticleCombinedFullDataDto get(String slug) {
		ArticleCombinedFullData data = articleService.findFullDataBySlug(slug);
		ArticleCombinedFullDataDto result = new ArticleCombinedFullDataDto();
		result.setArticle(data.getArticle());
		result.setAuthor(linkToAuthor(data.getAuthor()));
		result.setBody(data.getBody());
		result.setFavorited(data.isFavorited());
		result.setFavoritesCount(data.getFavoritesCount());
		result.setTagList(data.getTagList());
		return result;
	}

	@Override
	public SearchResult<ArticleSearchResultDto> find(String tag, String author, String favoritedBy, Integer limit, Integer offset) {
		ArticleSearchCriteria criteria = ImmutableArticleSearchCriteria.builder()
				.tag(tag)
				.addAuthors(author)
				.favoritedBy(favoritedBy)
				.limit(limit)
				.offset(offset)
				.build();
		SearchResult<ArticleSearchResult> searchResult = articleService.find(criteria);
		return new SearchResult<>(searchResult.getCount(), searchResult.getResults().stream().map(a -> {
			ArticleSearchResultDto d = new ArticleSearchResultDto();
			d.setArticle(a.getArticle());
			d.setAuthor(linkToAuthor(a.getAuthor()));
			d.setTagList(a.getTagList());
			d.setFavorited(a.isFavorited());
			d.setFavoritesCount(a.getFavoritesCount());
			d.setHref(uriInfo.getRequestUriBuilder().path(ArticlesResource.class, "get").build(a.getArticle().getSlug()).toString());
			return d;
		}).collect(Collectors.toList()));
	}

	private ResourceLink linkToAuthor(NameAndId author) {
		return new ResourceLink(author.getName(), uriInfo.getRequestUriBuilder().uri(config.getUserUrlTemplate()).build(author.getName()).toString());
	}
}
