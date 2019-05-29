package realworld.article.jaxrs;

import java.util.Set;

import realworld.article.model.ArticleCreationData;

/**
 * Concrete article creation data.
 */
public class ArticleCreationParam implements ArticleCreationData {

	private String title;

	private String description;

	private String body;

	private Set<String> tagList;

	private String authorId;

	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title.
	 *
	 * @param title The title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 *
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getBody() {
		return body;
	}

	/**
	 * Set the article body.
	 *
	 * @param body The article body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public Set<String> getTagList() {
		return tagList;
	}

	/**
	 * Set the tags for this article.
	 *
	 * @param tagList The tags for this article
	 */
	public void setTagList(Set<String> tagList) {
		this.tagList = tagList;
	}

	@Override
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * Set the author id.
	 *
	 * @param authorId The author id
	 */
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
}
