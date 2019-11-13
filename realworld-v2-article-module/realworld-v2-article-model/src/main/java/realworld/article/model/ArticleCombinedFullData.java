package realworld.article.model;

import java.util.Set;

import realworld.NameAndId;

/**
 * Article full data.
 */
public class ArticleCombinedFullData {

	private ArticleBase article;

	private String body;

	private Set<String> tagList;

	private boolean favorited;

	private int favoritesCount;

	private NameAndId author;

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

	/**
	 * Get the author.
	 *
	 * @return The author name and id
	 */
	public NameAndId getAuthor() {
		return author;
	}

	/**
	 * Set the author.
	 *
	 * @param author The author name and id
	 */
	public void setAuthor(NameAndId author) {
		this.author = author;
	}
}
