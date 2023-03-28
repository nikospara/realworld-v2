package realworld.article.services.v1.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import realworld.article.model.v1.Article;
import realworld.article.model.v1.ArticleBody;
import realworld.article.model.v1.Tag;
import realworld.model.common.v1.ArticleId;
import realworld.model.common.v1.AuthorId;
import realworld.model.common.v1.FormattedText;
import realworld.model.common.v1.StructuredText;

public class ArticleImpl implements Article {
	private final ArticleId id;
	private final String slug;
	private final FormattedText title;
	private final AuthorId authorId;
	private final StructuredText description;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private Supplier<Set<Tag>> tagSupplier;
	private Supplier<ArticleBody> bodySupplier;
	private IntSupplier favoritesCountSupplier;

	public ArticleImpl(ArticleId id, String slug, FormattedText title, AuthorId authorId, StructuredText description, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.slug = slug;
		this.title = title;
		this.authorId = authorId;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public ArticleId getId() {
		return id;
	}

	@Override
	public String getSlug() {
		return slug;
	}

	@Override
	public FormattedText getTitle() {
		return title;
	}

	@Override
	public AuthorId getAuthorId() {
		return authorId;
	}

	@Override
	public StructuredText getDescription() {
		return description;
	}

	@Override
	public ArticleBody getArticleBody() {
		return bodySupplier.get();
	}

	@Override
	public Set<Tag> getTags() {
		return tagSupplier.get();
	}

	@Override
	public int getFavoritesCount() {
		return favoritesCountSupplier.getAsInt();
	}

	@Override
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public ArticleImpl withTagSupplier(Supplier<Set<Tag>> tagSupplier) {
		this.tagSupplier = tagSupplier;
		return this;
	}

	public ArticleImpl withBodySupplier(Supplier<ArticleBody> bodySupplier) {
		this.bodySupplier = bodySupplier;
		return this;
	}

	public ArticleImpl withFavoritesCountSupplier(IntSupplier favoritesCountSupplier) {
		this.favoritesCountSupplier = favoritesCountSupplier;
		return this;
	}
}
