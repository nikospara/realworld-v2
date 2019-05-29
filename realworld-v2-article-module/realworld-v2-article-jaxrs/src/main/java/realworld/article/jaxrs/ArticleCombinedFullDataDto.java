package realworld.article.jaxrs;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import realworld.ResourceLink;
import realworld.article.model.ArticleBase;

/**
 * Article full data.
 */
public class ArticleCombinedFullDataDto {

	@JsonUnwrapped
	private ArticleBase article;

	private String body;

	private Set<String> tagList;

	private boolean favorited;

	private int favoritesCount;

	private ResourceLink author;

	/**
	 * Get the main article data.
	 *
	 * @return The main article data
	 */
	public ArticleBase getArticle() {
		return article;
	}

	public void setArticle(ArticleBase article) {
		this.article = article;
	}

	/**
	 * Get the article body.
	 *
	 * @return The article body
	 */
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Get the tags for this article.
	 *
	 * @return The tags for this article
	 */
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
}
