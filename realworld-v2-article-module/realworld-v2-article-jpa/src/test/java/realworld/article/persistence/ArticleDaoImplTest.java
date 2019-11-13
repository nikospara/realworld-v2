package realworld.article.persistence;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

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
import realworld.NameAndId;
import realworld.SearchResult;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ImmutableArticleSearchCriteria;
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
	private static final String AUTHOR_NAME = "AUTHOR NAME";
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
		Tag tag1 = new Tag("tag1");
		em.persist(tag1);
		User user = new User(AUTHOR_ID, AUTHOR_NAME);
		em.persist(user);
		em.flush();
		ArticleCreationData creationData = mock(ArticleCreationData.class);
		when(creationData.getAuthorId()).thenReturn(AUTHOR_ID);
		when(creationData.getBody()).thenReturn(BODY);
		when(creationData.getDescription()).thenReturn(DESCRIPTION);
		when(creationData.getTagList()).thenReturn(new HashSet<>(Arrays.asList("tag1", "tag2")));
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
		assertEquals(AUTHOR_ID, res.getAuthor().getId());
		assertEquals(AUTHOR_NAME, res.getAuthor().getName());
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
		assertEquals(AUTHOR_ID, res.getAuthor().getId());
		assertEquals(AUTHOR_NAME, res.getAuthor().getName());
		assertEquals(CREATED_AT, res.getArticle().getCreatedAt());
		assertEquals(DESCRIPTION, res.getArticle().getDescription());
		assertEquals(SLUG, res.getArticle().getSlug());
		assertEquals(TITLE, res.getArticle().getTitle());
//		assertEquals(UPDATED_AT, res.getArticle().getUpdatedAt());
		assertEquals(BODY, res.getBody());
	}

	@Test
	@Order(6)
	void testFindArticleIdBySlug() {
		String articleId = sut.findArticleIdBySlug(SLUG);
		Set<String> tags = sut.findTags(articleId);
		assertEquals(new HashSet<>(Arrays.asList("tag1", "tag2")), tags);
	}

	@Test
	@Order(7)
	void testFindArticleIdBySlugThrowsWhenNotFound() {
		try {
			sut.findArticleIdBySlug("non-existing-slug");
			fail("should throw exception for non-existing entity");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
	}

	@Test
	@Order(100) // keep me last
	void testFind() {
		em.getTransaction().begin();
		Article a1 = createArticle("ar1", AUTHOR_ID);
		Article a2 = createArticle("ar2", "y", "tag1");
		Article a3 = createArticle("ar3", "z", "tag2");
		favoriteArticle(a1, "u");
		favoriteArticle(a3, "u");
		em.getTransaction().commit();

		ArticleSearchCriteria c1 = ImmutableArticleSearchCriteria.builder().tag("tag1").offset(0).limit(10).build();
		SearchResult<ArticleSearchResult> r1 = sut.find(null, c1);
		assertNotNull(r1);
		assertEquals(2L, r1.getCount());
		assertEquals(Arrays.asList(AUTHOR_ID, "y"), sortedAuthorIds(r1));
		assertEquals(AUTHOR_NAME, r1.getResults().stream().map(ArticleSearchResult::getAuthor).filter(a -> a.getId().equals(AUTHOR_ID)).map(NameAndId::getName).findFirst().get());

		ArticleSearchCriteria c2 = ImmutableArticleSearchCriteria.builder().favoritedBy("u").offset(0).limit(10).build();
		SearchResult<ArticleSearchResult> r2 = sut.find(null, c2);
		assertEquals(2L, r2.getCount());
		assertEquals(Arrays.asList(a1.getSlug(), a3.getSlug()), sortedSlugs(r2));
		assertEquals(Arrays.asList(AUTHOR_ID, "z"), sortedAuthorIds(r2));

		ArticleSearchCriteria c2limited = ImmutableArticleSearchCriteria.builder().favoritedBy("u").offset(0).limit(1).build();
		SearchResult<ArticleSearchResult> r2limited = sut.find(null, c2limited);
		assertEquals(2L, r2limited.getCount());
		assertEquals(1, r2limited.getResults().size());

		ArticleSearchCriteria c3 = ImmutableArticleSearchCriteria.builder().tag("tag2").favoritedBy("u").offset(0).limit(10).build();
		SearchResult<ArticleSearchResult> r3 = sut.find(null, c3);
		assertNotNull(r3);
		assertEquals(Collections.singletonList(a3.getSlug()), sortedSlugs(r3));

		ArticleSearchCriteria c4 = ImmutableArticleSearchCriteria.builder().tag("tag2").offset(0).limit(10).build();
		SearchResult<ArticleSearchResult> r4 = sut.find("u", c4);
		assertNotNull(r4);
		assertEquals(2L, r4.getCount());
		assertEquals(Arrays.asList(a3.getSlug(), SLUG), sortedSlugs(r4));
		Map<String, ArticleSearchResult> resultsById = r4.getResults().stream().collect(toMap(asr -> asr.getArticle().getId(), Function.identity()));
		assertTrue(resultsById.get(a3.getId()).isFavorited());
		assertFalse(resultsById.values().stream().filter(asr -> !asr.getArticle().getId().equals(a3.getId())).anyMatch(ArticleSearchResult::isFavorited));

		// TODO Authors!!!
	}

	private Article createArticle(String slug, String authorId, String... tags) {
		Article a = new Article();
		a.setId(UUID.randomUUID().toString());
		a.setSlug(slug);
		a.setTitle(slug + " TITLE");
		a.setDescription(slug + " DESCR");
		a.setAuthorId(authorId);
		a.setCreatedAt(LocalDateTime.now());
		a.setTags(Arrays.stream(tags).map(t -> em.getReference(Tag.class, t)).collect(toSet()));
		em.persist(a);
		return a;
	}

	private void favoriteArticle(Article a, String userId) {
		ArticleFavorite f = new ArticleFavorite();
		f.setArticleId(a.getId());
		f.setUserId(userId);
		em.persist(f);
	}

	private List<String> sortedSlugs(SearchResult<ArticleSearchResult> r) {
		return r.getResults().stream().map(ArticleSearchResult::getArticle).map(ArticleBase::getSlug).sorted().collect(toList());
	}

	private List<String> sortedAuthorIds(SearchResult<ArticleSearchResult> r) {
		return r.getResults().stream().map(ArticleSearchResult::getAuthor).map(NameAndId::getId).sorted().collect(toList());
	}
}
