package realworld.article.services.v1.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import realworld.SearchResult;
import realworld.SimpleConstraintViolation;
import realworld.SimpleValidationException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.v1.Article;
import realworld.article.model.v1.ArticleSearchCriteria;
import realworld.article.model.v1.ArticleSearchResult;
import realworld.article.model.v1.ArticleUpsertData;
import realworld.article.model.v1.AuthenticatedUser;
import realworld.article.model.v1.Author;
import realworld.article.model.v1.User;
import realworld.article.services.v1.ArticleService;
import realworld.model.common.v1.FormattedText;
import realworld.services.DateTimeService;

public class ArticleServiceImpl implements ArticleService {
	private ArticleDao articleDao;

	private Function<FormattedText,String> unformatter;

	private Function<String,String> slugifier;

	private DateTimeService dateTimeService;

	@Override
	public SearchResult<ArticleSearchResult> search(AuthenticatedUser connectedUser, User userWhoSearches, ArticleSearchCriteria criteria) {
		return null;
	}

	@Override
	public Optional<ArticleSearchResult> fetchBySlug(AuthenticatedUser connectedUser, User userWhoSearches, String slug) {
		return Optional.empty();
	}

	@Override
	public Article create(AuthenticatedUser connectedUser, Author author, ArticleUpsertData data) {
		Objects.requireNonNull(author, "author cannot be null");
		Objects.requireNonNull(data, "creation data cannot be null");
		String unformattedTitle = unformatter.apply(data.getTitle());
		String slug = slugifier.apply(unformattedTitle);
		validateSlugDoesNotExist(slug);
		LocalDateTime createdAt = dateTimeService.getNow();
		String id = articleDao.create(data, slug, author.getId(), createdAt);
		return null;
	}

	private void validateSlugDoesNotExist(String slug) {
		if (articleDao.slugExists(slug)) {
			throw new SimpleValidationException(new SimpleConstraintViolation("slug", "duplicate slug"));
		}
	}

	@Override
	public Article update(AuthenticatedUser connectedUser, Author author, ArticleUpsertData data) {
		return null;
	}

	@Override
	public void delete(AuthenticatedUser connectedUser, String slug) {

	}
}
