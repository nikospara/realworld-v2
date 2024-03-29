package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import realworld.EntityDoesNotExistException;
import realworld.SearchResult;
import realworld.SimpleConstraintViolation;
import realworld.SimpleValidationException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleUpdateData;
import realworld.article.model.ImmutableArticleBase;
import realworld.article.model.ImmutableArticleSearchCriteria;
import realworld.article.services.ArticleService;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.authentication.AuthenticationContext;
import realworld.services.DateTimeService;

/**
 * Implementation of the {@link ArticleService}.
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
class ArticleServiceImpl implements ArticleService {

	private static final ArticleSearchCriteria DEFAULT_CRITERIA = ImmutableArticleSearchCriteria.builder().limit(20).offset(0).build();

	private ArticleDao articleDao;

	private Function<String,String> slugifier;

	private DateTimeService dateTimeService;

	private AuthenticationContext authenticationContext;

	/**
	 * Default constructor for the frameworks.
	 */
	@SuppressWarnings("unused")
	ArticleServiceImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param articleDao            The DAO
	 * @param slugifier             The service to turn a title to slug
	 * @param dateTimeService       The date/time service
	 * @param authenticationContext The authentication context to get connected user data
	 */
	@Inject
	public ArticleServiceImpl(ArticleDao articleDao, @Slugifier Function<String,String> slugifier, DateTimeService dateTimeService, AuthenticationContext authenticationContext) {
		this.articleDao = articleDao;
		this.slugifier = slugifier;
		this.dateTimeService = dateTimeService;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public ArticleBase create(ArticleCreationData creationData) {
		Objects.requireNonNull(creationData, "creationData cannot be null");
		String slug = slugifier.apply(creationData.getTitle());
		if (articleDao.slugExists(slug)) {
			throw new SimpleValidationException(Collections.singletonList(new SimpleConstraintViolation("slug", "duplicate slug")));
		}
		LocalDateTime createdAt = dateTimeService.getNow();
		String id = articleDao.create(creationData, slug, createdAt);
		return ImmutableArticleBase.builder()
				.id(id)
				.slug(slug)
				.title(creationData.getTitle())
				.description(creationData.getDescription())
				.createdAt(createdAt)
				.build();
	}

	@Override
	public String update(String slug, ArticleUpdateData updateData) {
		Objects.requireNonNull(updateData, "updateData cannot be null");
		return articleDao.update(slug, updateData, dateTimeService.getNow());
	}

	@Override
	public void delete(String slug) {
		articleDao.delete(slug);
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String slug) {
		ArticleCombinedFullData result = articleDao.findFullDataBySlug(authenticationContext.getUserPrincipal() != null ? authenticationContext.getUserPrincipal().getUniqueId() : null, slug);
		result.setTagList(articleDao.findTags(result.getArticle().getId()));
		return result;
	}

	@Override
	public SearchResult<ArticleSearchResult> find(ArticleSearchCriteria criteria) {
		return articleDao.find(authenticationContext.getUserPrincipal().getUniqueId(), mergeArticleSearchCriteria(DEFAULT_CRITERIA, criteria));
	}

	private ArticleSearchCriteria mergeArticleSearchCriteria(ArticleSearchCriteria defaultCriteria, ArticleSearchCriteria criteria) {
		return ImmutableArticleSearchCriteria.builder()
				.authors(criteria.getAuthors() != null ? criteria.getAuthors() : defaultCriteria.getAuthors())
				.favoritedBy(criteria.getFavoritedBy() != null ? criteria.getFavoritedBy() : defaultCriteria.getFavoritedBy())
				.tag(criteria.getTag() != null ? criteria.getTag() : defaultCriteria.getTag())
				.limit(criteria.getLimit() != null ? criteria.getLimit() : defaultCriteria.getLimit())
				.offset(criteria.getOffset() != null ? criteria.getOffset() : defaultCriteria.getOffset())
				.build();
	}
}
