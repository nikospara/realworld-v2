package realworld.comments.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import realworld.EntityDoesNotExistException;
import realworld.OrderBy;
import realworld.OrderByDirection;
import realworld.Paging;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentOrderBy;
import realworld.comments.model.ImmutableComment;
import realworld.persistence.jpa.JpaHelper;
import realworld.persistence.jpa.JpaHelperImpl;
import realworld.test.jpa.JpaDaoExtension;
import realworld.test.liquibase.LiquibaseExtension;

/**
 * Tests for the {@link CommentsDaoImpl}.
 */
@ExtendWith({LiquibaseExtension.class, JpaDaoExtension.class})
@EnabledIfSystemProperty(named = "database-test.active", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentsDaoImplTest {

	private static final String COMMENT_ID = UUID.randomUUID().toString();
	private static final String BODY = "Body";
	private static final String ARTICLE_ID = UUID.randomUUID().toString();
	private static final String OTHER_ARTICLE_ID = UUID.randomUUID().toString();
	private static final String ARTICLE_SLUG = "slug1";
	private static final String OTHER_ARTICLE_SLUG = "slug2";
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final LocalDateTime CREATED_AT = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.now();

	private EntityManager em;
	private CommentsDaoImpl sut;

	@BeforeEach
	void init(EntityManager em) {
		this.em = em;
		sut = new CommentsDaoImpl(em, new JpaHelperImpl(em));
	}

	@Test
	@Order(1)
	void testCreate() {
		em.getTransaction().begin();
		article(ARTICLE_ID, ARTICLE_SLUG);
		Comment comment = comment(COMMENT_ID, ARTICLE_ID, CREATED_AT);
		String commentId = sut.create(comment);
		em.getTransaction().commit();
		CommentEntity entity = em.find(CommentEntity.class, commentId);
		assertNotNull(entity);
		assertEquals(BODY, entity.getBody());
	}

	@Test
	@Order(2)
	void testFindById() {
		Comment comment = sut.findById(COMMENT_ID);
		assertNotNull(comment);
		assertEquals(BODY, comment.getBody());

		Comment nonExisting = sut.findById("non-existing");
		assertNull(nonExisting);
	}

	@Test
	@Order(3)
	void testDeleteAllForArticle() {
		em.getTransaction().begin();
		String randomArticleId = UUID.randomUUID().toString();
		article(randomArticleId, "random slug");
		article(OTHER_ARTICLE_ID, OTHER_ARTICLE_SLUG);
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		String id4 = UUID.randomUUID().toString();
		Comment c1 = comment(id1, randomArticleId, CREATED_AT.minusHours(1));
		Comment c2 = comment(id2, randomArticleId, CREATED_AT.minusHours(2));
		Comment c3 = comment(id3, OTHER_ARTICLE_ID, CREATED_AT.minusHours(3));
		Comment c4 = comment(id4, OTHER_ARTICLE_ID, CREATED_AT.minusHours(4));
		sut.create(c1);
		sut.create(c2);
		sut.create(c3);
		sut.create(c4);
		em.getTransaction().commit();

		em.getTransaction().begin();
		sut.deleteAllForArticle(randomArticleId);
		em.getTransaction().commit();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<CommentEntity> root = query.from(CommentEntity.class);
		query.select(cb.count(root)).where(cb.equal(root.get(CommentEntity_.articleId), randomArticleId));
		Long count = em.createQuery(query).getSingleResult();
		assertEquals(0L, count);
	}

	@Test
	@Order(4)
	void testCountCommentsForArticle() {
		assertEquals(0L, sut.countCommentsForArticle("non existing"));
		assertEquals(2L, sut.countCommentsForArticle(OTHER_ARTICLE_ID));
		assertEquals(1L, sut.countCommentsForArticle(ARTICLE_ID));
	}

	@Test
	@Order(5)
	void testFindCommentsForArticlePaged() {
		assertEquals(1, sut.findCommentsForArticlePaged(ARTICLE_SLUG, null).size());
		assertEquals(1, sut.findCommentsForArticlePaged(ARTICLE_SLUG, new Paging<>()).size());
		Paging<CommentOrderBy> paging = new Paging<>();
		paging.setLimit(1);
		paging.setOffset(0);
		paging.setOrderBy(new OrderBy<>(CommentOrderBy.CREATION_DATE, OrderByDirection.ASC));
		List<Comment> results1 = sut.findCommentsForArticlePaged(OTHER_ARTICLE_SLUG, paging);
		assertEquals(1, results1.size());
		paging.setOffset(1);
		List<Comment> results2 = sut.findCommentsForArticlePaged(OTHER_ARTICLE_SLUG, paging);
		assertEquals(1, results2.size());
		assertNotEquals(results1.get(0).getId(), results2.get(0).getId());
		assertTrue(results2.get(0).getCreatedAt().isAfter(results1.get(0).getCreatedAt()));
	}

	@Test
	@Order(100) // keep me last
	void testDelete() {
		sut.delete(COMMENT_ID);
		assertNull(em.find(CommentEntity.class, COMMENT_ID));
		assertThrows(EntityDoesNotExistException.class, () -> sut.delete(COMMENT_ID));
	}

	private ArticleEntity article(String id, String slug) {
		ArticleEntity a = new ArticleEntity();
		a.setId(id);
		a.setSlug(slug);
		em.persist(a);
		return a;
	}

	private Comment comment(String id, String articleId, LocalDateTime createdAt) {
		return ImmutableComment.builder()
				.id(id)
				.body(BODY)
				.createdAt(createdAt)
				.updatedAt(createdAt)
				.articleId(articleId)
				.authorId(AUTHOR_ID)
				.build();
	}
}
