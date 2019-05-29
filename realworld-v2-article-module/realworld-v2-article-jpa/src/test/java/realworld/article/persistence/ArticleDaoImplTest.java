package realworld.article.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import realworld.article.model.ArticleCreationData;
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
		ArticleCreationData creationData = mock(ArticleCreationData.class);
		when(creationData.getAuthorId()).thenReturn(AUTHOR_ID);
		when(creationData.getBody()).thenReturn(BODY);
		when(creationData.getDescription()).thenReturn(DESCRIPTION);
//		when(creationData.getTagList())
		when(creationData.getTitle()).thenReturn(TITLE);
		String id = sut.create(creationData, SLUG, CREATED_AT);
		em.getTransaction().commit();

		Article a = em.find(Article.class, id);
		assertNotNull(a);
	}

	@Test
	@Order(2)
	void testSlugExists() {
		assertTrue(sut.slugExists(SLUG));
		assertFalse(sut.slugExists("slug that doesnt exist"));
	}

	@Test
	@Order(3)
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
	@Order(4)
	void testFindFullDataBySlugForAnonymousUser() {
		ArticleCombinedFullData res = sut.findFullDataBySlug(null, SLUG);
		assertNotNull(res.getArticle().getId());
		assertEquals(AUTHOR_ID, res.getAuthorId());
		assertEquals(CREATED_AT, res.getArticle().getCreatedAt());
		assertEquals(DESCRIPTION, res.getArticle().getDescription());
		assertEquals(SLUG, res.getArticle().getSlug());
		assertEquals(TITLE, res.getArticle().getTitle());
//		assertEquals(UPDATED_AT, res.getArticle().getUpdatedAt());
		assertEquals(BODY, res.getBody());
	}

	@Test
	@Order(5)
	void testFindFullDataBySlugForUser() {
		ArticleCombinedFullData res = sut.findFullDataBySlug(UUID.randomUUID().toString(), SLUG);
		assertNotNull(res.getArticle().getId());
		assertEquals(AUTHOR_ID, res.getAuthorId());
		assertEquals(CREATED_AT, res.getArticle().getCreatedAt());
		assertEquals(DESCRIPTION, res.getArticle().getDescription());
		assertEquals(SLUG, res.getArticle().getSlug());
		assertEquals(TITLE, res.getArticle().getTitle());
//		assertEquals(UPDATED_AT, res.getArticle().getUpdatedAt());
		assertEquals(BODY, res.getBody());
	}
}
