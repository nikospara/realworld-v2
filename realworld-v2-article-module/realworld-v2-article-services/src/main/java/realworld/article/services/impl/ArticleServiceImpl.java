package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Function;

import realworld.EntityDoesNotExistException;
import realworld.SimpleConstraintViolation;
import realworld.SimpleValidationException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ImmutableArticleBase;
import realworld.article.services.ArticleService;
import realworld.authentication.AuthenticationContext;
import realworld.services.DateTimeService;

/**
 * Implementation of the
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
public class ArticleServiceImpl implements ArticleService {

	private ArticleDao articleDao;

	private Function<String,String> slugifier;

	private DateTimeService dateTimeService;

	private AuthenticationContext authenticationContext;

	/**
	 * Default constructor for the frameworks.
	 */
	ArticleServiceImpl() {
		// NOOP
	}

	@Inject
	public ArticleServiceImpl(ArticleDao articleDao, @Slugifier Function<String,String> slugifier, DateTimeService dateTimeService, AuthenticationContext authenticationContext) {
		this.articleDao = articleDao;
		this.slugifier = slugifier;
		this.dateTimeService = dateTimeService;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public ArticleBase create(ArticleCreationData creationData) {
		String slug = slugifier.apply(creationData.getTitle());
		if( articleDao.slugExists(slug) ) {
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
	public ArticleCombinedFullData findFullDataBySlug(String slug) {
		ArticleCombinedFullData result = articleDao.findFullDataBySlug(authenticationContext.getUserPrincipal() != null ? authenticationContext.getUserPrincipal().getUniqueId() : null, slug);
		// TODO fill-in missing details: tagList
		return result;
	}
}
