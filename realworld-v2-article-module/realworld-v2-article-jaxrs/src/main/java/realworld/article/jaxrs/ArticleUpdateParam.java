package realworld.article.jaxrs;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import realworld.article.model.ArticleUpdateData;

/**
 * Concrete article update data.
 */
public class ArticleUpdateParam implements ArticleUpdateData {

	private Optional<String> id;
	private Optional<String> title;
	private Optional<String> description;
	private Optional<String> body;
	private Optional<Set<String>> tagList;
	private Optional<String> authorId;
	private Optional<LocalDateTime> createdAt;
	private Optional<LocalDateTime> updatedAt;

	@Override
	public Optional<String> getId() {
		return id;
	}

	public void setId(Optional<String> id) {
		this.id = id;
	}

	@Override
	public Optional<String> getTitle() {
		return title;
	}

	public void setTitle(Optional<String> title) {
		this.title = title;
	}

	@Override
	public Optional<String> getDescription() {
		return description;
	}

	public void setDescription(Optional<String> description) {
		this.description = description;
	}

	@Override
	public Optional<String> getBody() {
		return body;
	}

	public void setBody(Optional<String> body) {
		this.body = body;
	}

	@Override
	public Optional<Set<String>> getTagList() {
		return tagList;
	}

	public void setTagList(Optional<Set<String>> tagList) {
		this.tagList = tagList;
	}

	@Override
	public Optional<String> getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Optional<String> authorId) {
		this.authorId = authorId;
	}

	@Override
	public Optional<LocalDateTime> getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Optional<LocalDateTime> createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public Optional<LocalDateTime> getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Optional<LocalDateTime> updatedAt) {
		this.updatedAt = updatedAt;
	}
}
