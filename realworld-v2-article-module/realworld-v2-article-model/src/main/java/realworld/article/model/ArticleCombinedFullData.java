package realworld.article.model;

import java.util.List;

import realworld.ResourceLink;

/**
 * Article full data.
 */
public class ArticleCombinedFullData {

	private ArticleBase article;

	private String body;

	private List<String> tagList;

	private boolean favorited;

	private int favoritesCount;

	private ResourceLink author;

	public ArticleBase getArticle() {
		return article;
	}

	public void setArticle(ArticleBase article) {
		this.article = article;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
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
