package realworld.article.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
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
		Root<Article> root = query.from(Article.class);
		query.select(root.get(Article_.slug));
		query.where(cb.equal(root.get(Article_.slug), slug));
		return !em.createQuery(query).setMaxResults(1).getResultList().isEmpty();
	}

	@Override
	public String create(ArticleCreationData creationData, String slug, LocalDateTime creationDate) {
		Article a = new Article();
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
			ArticleBody body = new ArticleBody();
			body.setBody(creationData.getBody());
			body.setArticle(a);
			em.persist(body);
		}
		return a.getId();
	}

	@Override
	public String update(String slug, ArticleUpdateData updateData, LocalDateTime updateTime) {
		Article a = findBySlug(slug);
		boolean changed = false;
		changed = updateFromOptional(updateData.getAuthorId(), a::setAuthorId, changed);
		changed = updateFromOptional(updateData.getCreatedAt(), a::setCreatedAt, changed);
		changed = updateFromOptional(updateData.getDescription(), a::setDescription, changed);
		changed = updateFromOptional(updateData.getTitle(), a::setTitle, changed);
		if( updateData.getBody() != null ) {
			ArticleBody body = em.find(ArticleBody.class, a.getId());
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
			Article a = (Article) res[0];
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
		Article article = Optional.ofNullable(em.find(Article.class, articleId)).orElseThrow(EntityDoesNotExistException::new);
		return article.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
	}

	@Override
	public String findArticleIdBySlug(String slug) {
		return findBySlug(String.class, slug, (query,article) -> query.select(article.get(Article_.id)));
	}

	private Article findBySlug(String slug) {
		return findBySlug(Article.class, slug, null);
	}

	private <X> X findBySlug(Class<X> x, String slug, BiConsumer<CriteriaQuery<X>, Root<Article>> querySpecializer) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<X> query = cb.createQuery(x);
			Root<Article> article = query.from(Article.class);
			query.where(cb.equal(article.get(Article_.slug), slug));
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
		Root<Article> article = query.from(Article.class);
		query.where(cb.equal(article.get(Article_.slug), slug));
		query.multiselect(
				article,
				favoritedSubquery(cb, query, article, userId),
				favoritesCountSubquery(cb, query, article),
				bodySubquery(cb, query, article),
				authorNameSubquery(cb, query, article)
		);
		return query;
	}

	private Expression<String> authorNameSubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Article> article) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<User> user = subquery.from(User.class);
		subquery
				.select(user.get(User_.username))
				.where(cb.equal(user.get(User_.id), article.get(Article_.authorId)));
		return cb.trim(subquery);
	}

	private Expression<String> bodySubquery(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Article> article) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<ArticleBody> articleBody = subquery.from(ArticleBody.class);
		subquery
				.select(articleBody.get(ArticleBody_.body))
				.where(cb.equal(articleBody.get(ArticleBody_.articleId), article.get(Article_.id)));
		return cb.trim(subquery);
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

	private void handleTags(Article article, Set<String> tags) {
		if( tags != null ) {
			Set<Tag> dbtags = tags.stream().map(tag -> Optional.ofNullable(em.find(Tag.class, tag)).orElseGet(() -> new Tag(tag))).collect(Collectors.toSet());
			article.setTags(dbtags);
		}
	}

	@Override
	public SearchResult<ArticleSearchResult> find(String userId, ArticleSearchCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<Article> articleRoot = applyCriteria(cb, query, criteria);
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

	private Root<Article> applyCriteria(CriteriaBuilder cb, CriteriaQuery<?> query, ArticleSearchCriteria criteria) {
		Root<Article> articleRoot = query.from(Article.class);
		var restrictions = new ArrayList<Predicate>();

		if( criteria.getTag() != null ) {
			restrictions.add(cb.isMember(new Tag(criteria.getTag()), articleRoot.get(Article_.tags)));
		}

		if( criteria.getAuthors() != null && !criteria.getAuthors().isEmpty() ) {
			restrictions.add(articleRoot.get(Article_.authorId).in(criteria.getAuthors()));
		}

		if( criteria.getFavoritedBy() != null && criteria.getFavoritedBy().trim().length() > 0  ) {
			Subquery<ArticleFavorite> favoriteSubquery = query.subquery(ArticleFavorite.class);
			Root<ArticleFavorite> favRoot = favoriteSubquery.from(ArticleFavorite.class);
			favoriteSubquery
					.select(favRoot)
					.where(
							cb.equal(favRoot.get(ArticleFavorite_.articleId), articleRoot.get(Article_.id)),
							cb.equal(favRoot.get(ArticleFavorite_.userId), criteria.getFavoritedBy())
					);
			restrictions.add(cb.exists(favoriteSubquery));
		}

		query.where(restrictions.toArray(new Predicate[0]));

		return articleRoot;
	}

	private ArticleSearchResult fromQueryResult(Object[] result) {
		Article article = (Article) result[0];
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
