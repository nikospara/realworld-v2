package realworld.article.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import realworld.EntityDoesNotExistException;
import realworld.NameAndId;
import realworld.SearchResult;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.article.model.ImmutableArticleBase;
import realworld.article.model.ImmutableArticleSearchResult;

/**
 * JPA implementation of the {@link ArticleDao}.
 */
@ApplicationScoped
public class ArticleDaoImpl implements ArticleDao {

	private EntityManager em;

	/**
	 * Default constructor for the frameworks.
	 */
	@SuppressWarnings("unused")
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
	public boolean slugExists(String slug) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<ArticleEntity> root = query.from(ArticleEntity.class);
		query.select(root.get(ArticleEntity_.slug));
		query.where(cb.equal(root.get(ArticleEntity_.slug), slug));
		return !em.createQuery(query).setMaxResults(1).getResultList().isEmpty();
	}

	@Override
	public String create(ArticleCreationData creationData, String slug, LocalDateTime creationDate) {
		ArticleEntity a = new ArticleEntity();
		a.setId(UUID.randomUUID().toString());
		a.setTitle(creationData.getTitle());
		a.setSlug(slug);
		a.setDescription(creationData.getDescription());
		a.setCreatedAt(creationDate);
//		a.setUpdatedAt(creationDate);
		a.setAuthorId(creationData.getAuthorId());
		handleTags(a, creationData.getTagList());
		em.persist(a);
		if( creationData.getBody() != null ) {
			ArticleBodyEntity body = new ArticleBodyEntity();
			body.setBody(creationData.getBody());
			body.setArticle(a);
			em.persist(body);
		}
		return a.getId();
	}

	@Override
	public String update(String slug, ArticleUpdateData updateData, LocalDateTime updateTime) {
		ArticleEntity a = findBySlug(slug);
		boolean changed = false;
		changed = updateFromOptional(updateData.getAuthorId(), a::setAuthorId, changed);
		changed = updateFromOptional(updateData.getCreatedAt(), a::setCreatedAt, changed);
		changed = updateFromOptional(updateData.getDescription(), a::setDescription, changed);
		changed = updateFromOptional(updateData.getTitle(), a::setTitle, changed);
		if( updateData.getBody() != null ) {
			ArticleBodyEntity body = em.find(ArticleBodyEntity.class, a.getId());
			body.setBody(updateData.getBody().orElse(null));
			changed = true;
		}
		if( updateData.getTagList() != null ) {
			handleTags(a, updateData.getTagList().orElseGet(Collections::emptySet));
			changed = true;
		}
		if( changed || updateData.getUpdatedAt() != null ) {
			a.setUpdatedAt(updateData.getUpdatedAt() != null ? updateData.getUpdatedAt().orElse(updateTime) : updateTime);
		}
		return a.getId();
	}

	@Override
	public void delete(String slug) {
		ArticleEntity article = findBySlug(slug);
		if( article == null ) {
			throw new EntityDoesNotExistException();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaDelete<ArticleBodyEntity> deleteBody = em.getCriteriaBuilder().createCriteriaDelete(ArticleBodyEntity.class);
		Root<ArticleBodyEntity> bodyRoot = deleteBody.from(ArticleBodyEntity.class);
		deleteBody.where(cb.equal(bodyRoot.get(ArticleBodyEntity_.article), article));
		em.createQuery(deleteBody).executeUpdate();

		CriteriaDelete<ArticleFavoriteEntity> deleteFav = em.getCriteriaBuilder().createCriteriaDelete(ArticleFavoriteEntity.class);
		Root<ArticleFavoriteEntity> favRoot = deleteFav.from(ArticleFavoriteEntity.class);
		deleteFav.where(cb.equal(favRoot.get(ArticleFavoriteEntity_.articleId), article.getId()));
		em.createQuery(deleteFav).executeUpdate();

		em.remove(article);
	}

	private <X> boolean updateFromOptional(Optional<X> opt, Consumer<X> consumer, boolean changed) {
		if( opt != null ) {
			opt.ifPresentOrElse(consumer, () -> consumer.accept(null));
			return true;
		}
		else {
			return changed;
		}
	}

	@Override
	public ArticleCombinedFullData findFullDataBySlug(String userId, String slug) {
		try {
			Object[] res = em.createQuery(findArticleBySlugCriteriaQuery(userId, slug)).getSingleResult();
			ArticleEntity a = (ArticleEntity) res[0];
			ArticleCombinedFullData result = new ArticleCombinedFullData();
			result.setArticle(fromArticle(a));
			result.setAuthor(new NameAndId((String) res[4], a.getAuthorId()));
			result.setFavorited((Boolean) res[1]);
			result.setFavoritesCount(((Long) res[2]).intValue());
			result.setBody((String) res[3]);
			return result;
		}
		catch( NoResultException e ) {
			throw new EntityDoesNotExistException();
		}
	}

	@Override
	public Set<String> findTags(String articleId) {
		ArticleEntity article = Optional.ofNullable(em.find(ArticleEntity.class, articleId)).orElseThrow(EntityDoesNotExistException::new);
		return article.getTags().stream().map(TagEntity::getName).collect(Collectors.toSet());
	}

	@Override
	public String findArticleIdBySlug(String slug) {
		return findBySlug(String.class, slug, (query,article) -> query.select(article.get(ArticleEntity_.id)));
	}

	private ArticleEntity findBySlug(String slug) {
		return findBySlug(ArticleEntity.class, slug, null);
	}

	private <X> X findBySlug(Class<X> x, String slug, BiConsumer<CriteriaQuery<X>, Root<ArticleEntity>> querySpecializer) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<X> query = cb.createQuery(x);
			Root<ArticleEntity> article = query.from(ArticleEntity.class);
			query.where(cb.equal(article.get(ArticleEntity_.slug), slug));
			if( querySpecializer != null ) {
				querySpecializer.accept(query, article);
			}
			return em.createQuery(query).getSingleResult();
		}
		catch( NoResultException e ) {
			throw new EntityDoesNotExistException();
		}
	}

	private CriteriaQuery<Object[]> findArticleBySlugCriteriaQuery(String userId, String slug) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<ArticleEntity> article = query.from(ArticleEntity.class);
		query.where(cb.equal(article.get(ArticleEntity_.slug), slug));
		query.multiselect(
				article,
				favoritedSubquery(cb, query, article, userId),
				favoritesCountSubquery(cb, query, article),
				bodySubquery(cb, query, article),
				authorNameSubquery(cb, query, article)
		);
		return query;
	}

	private Expression<String> authorNameSubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<ArticleEntity> article) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<UserEntity> user = subquery.from(UserEntity.class);
		subquery
				.select(user.get(UserEntity_.username))
				.where(cb.equal(user.get(UserEntity_.id), article.get(ArticleEntity_.authorId)));
		return cb.trim(subquery);
	}

	private Expression<String> bodySubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<ArticleEntity> article) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<ArticleBodyEntity> articleBody = subquery.from(ArticleBodyEntity.class);
		subquery
				.select(articleBody.get(ArticleBodyEntity_.body))
				.where(cb.equal(articleBody.get(ArticleBodyEntity_.articleId), article.get(ArticleEntity_.id)));
		return cb.trim(subquery);
	}

	/**
	 * Query whether the given user has favorited an article.
	 */
	private Expression<Boolean> favoritedSubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<ArticleEntity> article, String userId) {
		if( userId == null ) {
			return cb.literal(false);
		}
		else {
			Subquery<Long> subquery = query.subquery(Long.class);
			Root<ArticleFavoriteEntity> articleFavorite = subquery.from(ArticleFavoriteEntity.class);
			subquery
					.select(cb.count(articleFavorite))
					.where(
							cb.equal(articleFavorite.get(ArticleFavoriteEntity_.articleId), article.get(ArticleEntity_.id)),
							cb.equal(articleFavorite.get(ArticleFavoriteEntity_.userId), userId)
					);
			return cb.greaterThan(subquery, 0L);
		}
	}

	private Expression<Long> favoritesCountSubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<ArticleEntity> article) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<ArticleFavoriteEntity> articleFavorite = subquery.from(ArticleFavoriteEntity.class);
		subquery
				.select(cb.count(articleFavorite))
				.where(cb.equal(articleFavorite.get(ArticleFavoriteEntity_.articleId), article.get(ArticleEntity_.id)));
		return cb.sum(subquery, 0L);
	}

	private ArticleBase fromArticle(ArticleEntity a) {
		return ImmutableArticleBase.builder()
				.id(a.getId())
				.slug(a.getSlug())
				.title(a.getTitle())
				.description(a.getDescription())
				.createdAt(a.getCreatedAt())
				.updatedAt(a.getUpdatedAt())
				.build();
	}

	private void handleTags(ArticleEntity article, Set<String> tags) {
		if( tags != null ) {
			Set<TagEntity> dbtags = tags.stream().map(tag -> Optional.ofNullable(em.find(TagEntity.class, tag)).orElseGet(() -> new TagEntity(tag))).collect(Collectors.toSet());
			article.setTags(dbtags);
		}
	}

	@Override
	public SearchResult<ArticleSearchResult> find(String userId, ArticleSearchCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<ArticleEntity> articleRoot = applyCriteria(cb, query, criteria);
		query.multiselect(
				articleRoot,
				favoritedSubquery(cb, query, articleRoot, userId),
				favoritesCountSubquery(cb, query, articleRoot),
				authorNameSubquery(cb, query, articleRoot)
		);

		var results = em.createQuery(query)
				.setMaxResults(criteria.getLimit())
				.setFirstResult(criteria.getOffset())
				.getResultStream()
				.map(this::fromQueryResult)
				.collect(Collectors.toList());

		long count = results.size();
		if( count >= criteria.getLimit() || criteria.getOffset() != 0 ) {
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			applyCriteria(cb, countQuery, criteria);
			count = em.createQuery(countQuery.select(cb.count(countQuery.getRoots().iterator().next()))).getSingleResult();
		}

		return new SearchResult<>(count, results);
	}

	private Root<ArticleEntity> applyCriteria(CriteriaBuilder cb, CriteriaQuery<?> query, ArticleSearchCriteria criteria) {
		Root<ArticleEntity> articleRoot = query.from(ArticleEntity.class);
		var restrictions = new ArrayList<Predicate>();

		if( criteria.getTag() != null ) {
			restrictions.add(cb.isMember(new TagEntity(criteria.getTag()), articleRoot.get(ArticleEntity_.tags)));
		}

		if( criteria.getAuthors() != null && !criteria.getAuthors().isEmpty() ) {
			restrictions.add(articleRoot.get(ArticleEntity_.authorId).in(criteria.getAuthors()));
		}

		if( criteria.getFavoritedBy() != null && criteria.getFavoritedBy().trim().length() > 0  ) {
			Subquery<ArticleFavoriteEntity> favoriteSubquery = query.subquery(ArticleFavoriteEntity.class);
			Root<ArticleFavoriteEntity> favRoot = favoriteSubquery.from(ArticleFavoriteEntity.class);
			favoriteSubquery
					.select(favRoot)
					.where(
							cb.equal(favRoot.get(ArticleFavoriteEntity_.articleId), articleRoot.get(ArticleEntity_.id)),
							cb.equal(favRoot.get(ArticleFavoriteEntity_.userId), criteria.getFavoritedBy())
					);
			restrictions.add(cb.exists(favoriteSubquery));
		}

		query.where(restrictions.toArray(new Predicate[0]));

		return articleRoot;
	}

	private ArticleSearchResult fromQueryResult(Object[] result) {
		ArticleEntity article = (ArticleEntity) result[0];
		boolean isFavorited = (Boolean) result[1];
		int favoritesCount = ((Long) result[2]).intValue();
		String authorName = (String) result[3];
		return ImmutableArticleSearchResult.builder()
				.article(fromArticle(article))
				.isFavorited(isFavorited)
				.favoritesCount(favoritesCount)
				.author(new NameAndId(authorName, article.getAuthorId()))
				.build();
	}
}
