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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import realworld.article.model.ArticleUpdateData;
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
	private static final LocalDateTime UPDATED_AT = LocalDateTime.now().minus(12, ChronoUnit.HOURS);
	private static final String DESCRIPTION = "Description";
	private static final String SLUG = "slug";
	private static final String TITLE = "Title";
	private static final String BODY = "Body";

	private static final String SLUG_FOR_UPDATE = "updated-slug";
	private static final String UPDATED_AUTHOR_ID = UUID.randomUUID().toString();
	//	private static final String UPDATED_AUTHOR_NAME = "AUTHOR NAME";
	private static final LocalDateTime UPDATED_CREATED_AT = LocalDateTime.now().minus(6, ChronoUnit.HOURS);
	private static final LocalDateTime UPDATED_UPDATED_AT = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
	private static final String UPDATED_DESCRIPTION = "Updated Description";
	private static final String UPDATED_TITLE = "Updated Title";
	private static final String UPDATED_BODY = "Updated Body";

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
		TagEntity tag1 = new TagEntity("tag1");
		em.persist(tag1);
		UserEntity user = new UserEntity(AUTHOR_ID, AUTHOR_NAME);
		em.persist(user);
		em.flush();
		ArticleCreationData creationData = makeCreationData();
		String id = sut.create(creationData, SLUG, CREATED_AT);
		em.getTransaction().commit();

		ArticleEntity a = em.find(ArticleEntity.class, id);
		assertNotNull(a);
	}

	private ArticleCreationData makeCreationData() {
		ArticleCreationData creationData = mock(ArticleCreationData.class);
		when(creationData.getAuthorId()).thenReturn(AUTHOR_ID);
		when(creationData.getBody()).thenReturn(BODY);
		when(creationData.getDescription()).thenReturn(DESCRIPTION);
		when(creationData.getTagList()).thenReturn(new HashSet<>(Arrays.asList("tag1", "tag2")));
		when(creationData.getTitle()).thenReturn(TITLE);
		return creationData;
	}

	@Test
	@Order(2)
	void testUpdate() {
		em.getTransaction().begin();
		ArticleCreationData creationData = makeCreationData();
		sut.create(creationData, SLUG_FOR_UPDATE, CREATED_AT);
		em.getTransaction().commit();

		em.getTransaction().begin();
		ArticleUpdateData d = mock(ArticleUpdateData.class);
		when(d.getAuthorId()).thenReturn(Optional.of(UPDATED_AUTHOR_ID));
		when(d.getUpdatedAt()).thenReturn(Optional.of(UPDATED_UPDATED_AT));
		when(d.getCreatedAt()).thenReturn(Optional.of(UPDATED_CREATED_AT));
		when(d.getBody()).thenReturn(Optional.of(UPDATED_BODY));
		when(d.getDescription()).thenReturn(Optional.of(UPDATED_DESCRIPTION));
		when(d.getTagList()).thenReturn(Optional.of(new HashSet<>(Arrays.asList("tag4", "tag5"))));
		when(d.getTitle()).thenReturn(Optional.of(UPDATED_TITLE));
		String id = sut.update(SLUG_FOR_UPDATE, d, UPDATED_AT);
		em.getTransaction().commit();

		ArticleEntity a = em.find(ArticleEntity.class, id);
		assertNotNull(a);
		assertEquals(SLUG_FOR_UPDATE, a.getSlug());
		assertEquals(UPDATED_AUTHOR_ID, a.getAuthorId());
		assertEquals(UPDATED_UPDATED_AT, a.getUpdatedAt());
		assertEquals(UPDATED_CREATED_AT, a.getCreatedAt());
		assertEquals(UPDATED_BODY, em.find(ArticleBodyEntity.class, a.getId()).getBody());
		assertEquals(UPDATED_DESCRIPTION, a.getDescription());
		assertEquals(new HashSet<>(Arrays.asList("tag4", "tag5")), a.getTags().stream().map(TagEntity::getName).collect(Collectors.toSet()));
		assertEquals(UPDATED_TITLE, a.getTitle());
	}

	@Test
	@Order(3)
	void testDelete() {
		em.getTransaction().begin();
		ArticleFavoriteEntity fav = new ArticleFavoriteEntity();
		fav.setArticleId(sut.findArticleIdBySlug(SLUG_FOR_UPDATE));
		fav.setUserId(AUTHOR_ID);
		em.persist(fav);
		em.getTransaction().commit();

		statistics.clear();
		em.getTransaction().begin();
		sut.delete(SLUG_FOR_UPDATE);
		em.getTransaction().commit();

		try {
			em.getTransaction().begin();
			sut.delete(SLUG_FOR_UPDATE);
			fail("should throw when trying to delete non-existing slug");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
		finally {
			em.getTransaction().rollback();
		}
	}

	@Test
	@Order(4)
	void testSlugExists() {
		assertTrue(sut.slugExists(SLUG));
		assertFalse(sut.slugExists("slug that doesnt exist"));
	}

	@Test
	@Order(5)
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
	@Order(6)
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
	@Order(7)
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
	@Order(8)
	void testFindArticleIdBySlug() {
		String articleId = sut.findArticleIdBySlug(SLUG);
		Set<String> tags = sut.findTags(articleId);
		assertEquals(new HashSet<>(Arrays.asList("tag1", "tag2")), tags);
	}

	@Test
	@Order(9)
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
		ArticleEntity a1 = createArticle("ar1", AUTHOR_ID);
		ArticleEntity a2 = createArticle("ar2", "y", "tag1");
		ArticleEntity a3 = createArticle("ar3", "z", "tag2");
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

	private ArticleEntity createArticle(String slug, String authorId, String... tags) {
		ArticleEntity a = new ArticleEntity();
		a.setId(UUID.randomUUID().toString());
		a.setSlug(slug);
		a.setTitle(slug + " TITLE");
		a.setDescription(slug + " DESCR");
		a.setAuthorId(authorId);
		a.setCreatedAt(LocalDateTime.now());
		a.setTags(Arrays.stream(tags).map(t -> em.getReference(TagEntity.class, t)).collect(toSet()));
		em.persist(a);
		return a;
	}

	private void favoriteArticle(ArticleEntity a, String userId) {
		ArticleFavoriteEntity f = new ArticleFavoriteEntity();
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
