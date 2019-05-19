package realworld.article.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import realworld.EntityDoesNotExistException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ImmutableArticleBase;

/**
 * JPA implementation of the {@link ArticleDao}.
 */
@ApplicationScoped
public class ArticleDaoImpl implements ArticleDao {

	private EntityManager em;

	/**
	 * Default constructor for the frameworks.
	 */
	ArticleDaoImpl() {
		// NOOP
	}

	/**
	 * Dependency injection constructor.
	 *
	 * @param em The entity manager
	 */
	@Inject
	public ArticleDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String userId, String slug) {
		try {
			Object[] res = em.createQuery(findArticleBySlugCriteriaQuery(userId, slug)).getSingleResult();
			Article a = (Article) res[0];
			ArticleCombinedFullData result = new ArticleCombinedFullData();
			result.setArticle(fromArticle(a));
			result.setAuthorId(a.getAuthorId());
			result.setFavorited((Boolean) res[1]);
			result.setFavoritesCount(((Long) res[2]).intValue());
			result.setBody((String) res[3]);
			return result;
		}
		catch( NoResultException e ) {
			throw new EntityDoesNotExistException();
		}
	}

	private CriteriaQuery<Object[]> findArticleBySlugCriteriaQuery(String userId, String slug) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<Article> article = query.from(Article.class);
		query.where(cb.equal(article.get(Article_.slug), slug));
		query.multiselect(
				article,
				favoritedSubquery(cb, query, article, userId),
				favoritesCountSubquery(cb, query, article),
				bodySubquery(cb, query, article)
		);
		return query;
	}

	private Expression<String> bodySubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Article> article) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<ArticleBody> articleBody = subquery.from(ArticleBody.class);
		subquery
				.select(articleBody.get(ArticleBody_.body))
				.where(cb.equal(articleBody.get(ArticleBody_.articleId), article.get(Article_.id)));
		return subquery;
	}

	/**
	 * Query whether the given user has favorited an article.
	 */
	private Expression<Boolean> favoritedSubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Article> article, String userId) {
		if( userId == null ) {
			return cb.literal(false);
		}
		else {
			Subquery<Long> subquery = query.subquery(Long.class);
			Root<ArticleFavorite> articleFavorite = subquery.from(ArticleFavorite.class);
			subquery
					.select(cb.count(articleFavorite))
					.where(
							cb.equal(articleFavorite.get(ArticleFavorite_.articleId), article.get(Article_.id)),
							cb.equal(articleFavorite.get(ArticleFavorite_.userId), userId)
					);
			return cb.greaterThan(subquery, 0L);
		}
	}

	private Expression<Long> favoritesCountSubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Article> article) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<ArticleFavorite> articleFavorite = subquery.from(ArticleFavorite.class);
		subquery
				.select(cb.count(articleFavorite))
				.where(cb.equal(articleFavorite.get(ArticleFavorite_.articleId), article.get(Article_.id)));
		return cb.sum(subquery, 0L);
	}

	private ArticleBase fromArticle(Article a) {
		return ImmutableArticleBase.builder()
				.id(a.getId())
				.slug(a.getSlug())
				.title(a.getTitle())
				.description(a.getDescription())
				.createdAt(a.getCreatedAt())
				.updatedAt(a.getUpdatedAt())
				.build();
	}
}
