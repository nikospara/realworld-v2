package realworld.article.jaxrs;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import realworld.ResourceLink;
import realworld.article.model.ArticleBase;

/**
 * An article in the search results.
 */
public class ArticleSearchResultDto {

	@JsonUnwrapped
	private ArticleBase article;

	private Set<String> tagList;

	private boolean favorited;

	private int favoritesCount;

	private ResourceLink author;

	private String href;

	public ArticleBase getArticle() {
		return article;
	}

	public void setArticle(ArticleBase article) {
		this.article = article;
	}

	public Set<String> getTagList() {
		return tagList;
	}

	public void setTagList(Set<String> tagList) {
		this.tagList = tagList;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public int getFavoritesCount() {
		return favoritesCount;
	}

	public void setFavoritesCount(int favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public ResourceLink getAuthor() {
		return author;
	}

	public void setAuthor(ResourceLink author) {
		this.author = author;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
