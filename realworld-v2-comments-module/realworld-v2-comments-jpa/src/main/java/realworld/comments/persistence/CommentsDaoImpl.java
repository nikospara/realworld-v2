package realworld.comments.persistence;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import realworld.EntityDoesNotExistException;
import realworld.Paging;
import realworld.comments.dao.CommentsDao;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;
import realworld.comments.model.ImmutableComment;
import realworld.persistence.jpa.JpaHelper;

/**
 * Implementation of the {@link CommentsDao}.
 */
public class CommentsDaoImpl implements CommentsDao {

	private EntityManager em;
	private JpaHelper helper;

	/**
	 * Injection constructor.
	 *
	 * @param em     The entity manager
	 * @param helper The JPA helper
	 */
	@Inject
	public CommentsDaoImpl(EntityManager em, JpaHelper helper) {
		this.em = em;
		this.helper = helper;
	}

	/**
	 * Default constructor needed by frameworks.
	 */
	@SuppressWarnings("unused")
	CommentsDaoImpl() {
		// NOOP
	}

	@Override
	public String create(Comment comment) {
		CommentEntity entity = new CommentEntity();
		entity.setId(comment.getId() != null ? comment.getId() : UUID.randomUUID().toString());
		entity.setBody(comment.getBody());
		entity.setCreatedAt(comment.getCreatedAt());
		entity.setUpdatedAt(comment.getUpdatedAt());
		entity.setArticleId(comment.getArticleId());
		entity.setAuthorId(comment.getAuthorId());
		em.persist(entity);
		return entity.getId();
	}

	@Override
	public Comment findById(String id) {
		CommentEntity entity = em.find(CommentEntity.class, id);
		if( entity == null ) {
			return null;
		}
		return toComment(entity);
	}

	@Override
	public void delete(String id) {
		CommentEntity entity = em.find(CommentEntity.class, id);
		if( entity == null ) {
			throw new EntityDoesNotExistException(id);
		}
		em.remove(entity);
	}

	@Override
	public void deleteAllForArticle(String articleId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<CommentEntity> cd = cb.createCriteriaDelete(CommentEntity.class);
		Root<CommentEntity> root = cd.from(CommentEntity.class);
		cd.where(cb.equal(root.get(CommentEntity_.articleId), articleId));
		em.createQuery(cd).executeUpdate();
	}

	@Override
	public long countCommentsForArticle(String articleId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<CommentEntity> commentEntity = query.from(CommentEntity.class);
		query.select(cb.count(commentEntity)).where(cb.equal(commentEntity.get(CommentEntity_.articleId), articleId));
		return em.createQuery(query).getSingleResult();
	}

	@Override
	public List<Comment> findCommentsForArticlePaged(String slug, Paging<CommentOrderBy> paging) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CommentEntity> query = cb.createQuery(CommentEntity.class);
		Root<CommentEntity> commentEntity = query.from(CommentEntity.class);
		Subquery<String> articleIdSubquery = query.subquery(String.class);
		Root<ArticleEntity> articleEntity = articleIdSubquery.from(ArticleEntity.class);
		articleIdSubquery
				.select(articleEntity.get(ArticleEntity_.id))
				.where(cb.equal(articleEntity.get(ArticleEntity_.slug), slug));
		query.where(cb.equal(commentEntity.get(CommentEntity_.articleId), articleIdSubquery));
		TypedQuery<CommentEntity> typedQuery = helper.applyPaging(cb, query, paging, makeOrderByMapper(commentEntity));
		return typedQuery.getResultStream().map(this::toComment).collect(Collectors.toList());
	}

	@Override
	public Optional<String> findArticleIdForSlug(String slug) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<ArticleEntity> article = query.from(ArticleEntity.class);
		query.select(article.get(ArticleEntity_.id)).where(cb.equal(article.get(ArticleEntity_.slug), slug));
		TypedQuery<String> typedQuery = em.createQuery(query);
		return typedQuery.getResultStream().findFirst();
	}

	private Function<CommentOrderBy,Expression<?>> makeOrderByMapper(Root<CommentEntity> commentEntity) {
		return o -> {
			switch( o ) {
				case CREATION_DATE:
					return commentEntity.get(CommentEntity_.CREATED_AT);
				default:
					return null;
			}
		};
	}

	private Comment toComment(CommentEntity entity) {
		return ImmutableComment.builder()
				.id(entity.getId())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.authorId(entity.getAuthorId())
				.articleId(entity.getArticleId())
				.build();
	}
}
