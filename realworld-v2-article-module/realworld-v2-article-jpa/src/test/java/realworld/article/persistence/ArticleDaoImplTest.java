package realworld.article.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import realworld.EntityDoesNotExistException;
import realworld.article.model.ArticleCombinedFullData;
import realworld.test.jpa.JpaDaoExtension;
import realworld.test.liquibase.LiquibaseExtension;

/**
 * Tests for the {@link ArticleDaoImpl}.
 */
@ExtendWith({LiquibaseExtension.class, JpaDaoExtension.class})
@EnabledIfSystemProperty(named = "database-test.active", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArticleDaoImplTest {

	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final LocalDateTime CREATED_AT = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.now();
	private static final String DESCRIPTION = "Description";
	private static final String SLUG = "slug";
	private static final String TITLE = "Title";
	private static final String BODY = "Body";

	private EntityManager em;
	private Statistics statistics;
	private ArticleDaoImpl sut;

	@BeforeEach
	void init(EntityManager em, Statistics statistics) {
		this.em = em;
		this.statistics = statistics;
		sut = new ArticleDaoImpl(em);
	}

	@AfterEach
	void afterEach() {
		statistics.clear();
	}

	@Test
	@Order(1)
	void testCreate() {
		em.getTransaction().begin();
		// TODO Use DAO method when ready
		Article a = new Article();
		String articleId = UUID.randomUUID().toString();
		a.setId(articleId);
		a.setAuthorId(AUTHOR_ID);
		a.setCreatedAt(CREATED_AT);
		a.setDescription(DESCRIPTION);
		a.setSlug(SLUG);
		a.setTitle(TITLE);
		a.setUpdatedAt(UPDATED_AT);
		em.persist(a);
		ArticleBody b = new ArticleBody();
		b.setArticle(a);
		b.setBody(BODY);
		em.persist(b);
		em.getTransaction().commit();
	}

	@Test
	@Order(2)
	void testFindFullDataBySlugThrowsWhenNotFound() {
		try {
			sut.findFullDataBySlug(null, "non-existing-slug");
			fail("should throw exception for non-existing entity");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
	}

	@Test
	@Order(3)
	void testFindFullDataBySlug() {
		ArticleCombinedFullData res = sut.findFullDataBySlug(null, SLUG);
		assertNotNull(res.getArticle().getId());
		assertEquals(AUTHOR_ID, res.getAuthorId());
		assertEquals(CREATED_AT, res.getArticle().getCreatedAt());
		assertEquals(DESCRIPTION, res.getArticle().getDescription());
		assertEquals(SLUG, res.getArticle().getSlug());
		assertEquals(TITLE, res.getArticle().getTitle());
		assertEquals(UPDATED_AT, res.getArticle().getUpdatedAt());
		assertEquals(BODY, res.getBody());
	}
}
