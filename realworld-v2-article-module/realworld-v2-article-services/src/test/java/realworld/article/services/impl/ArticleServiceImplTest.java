package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.NameAndId;
import realworld.SearchResult;
import realworld.SimpleValidationException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.article.model.ArticleSearchCriteria;
import realworld.article.model.ArticleSearchResult;
import realworld.article.model.ArticleUpdateData;
import realworld.article.model.ImmutableArticleBase;
import realworld.article.model.ImmutableArticleSearchCriteria;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.services.DateTimeService;

/**
 * Tests for the {@link ArticleServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ArticleServiceImplTest {

	private static final String ARTICLE_ID = UUID.randomUUID().toString();
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final String AUTHOR_NAME = "AUTHOR NAME";
	private static final String USER_ID = UUID.randomUUID().toString();
	private static final LocalDateTime CREATED_AT = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.now();
	private static final String DESCRIPTION = "Description";
	private static final String SLUG = "slug";
	private static final String TITLE = "Title";
	private static final String BODY = "Body";
	private static final Set<String> TAG_LIST = Collections.singleton("tag1");
	private static final LocalDateTime NOW = LocalDateTime.now();

	@Produces @Mock
	private ArticleServiceAuthorizer authorizer;

	@Produces @Mock
	private ArticleDao articleDao;

	@Produces @Mock @Slugifier
	private Function<String,String> slugifier;

	@Produces @Mock(lenient = true)
	private DateTimeService dateTimeService;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private ArticleServiceImpl sut;

	@Test
	void testCreateWithNullData() {
		try {
			sut.create(null);
			fail("should fail on null creation data");
		}
		catch( NullPointerException expected ) {
			// expected
		}
		verifyNoInteractions(authorizer);
	}

	@Test
	void testCreateWithDuplicateSlug() {
		ArticleCreationData creationData = prepareForCreation();
		when(articleDao.slugExists(SLUG)).thenReturn(true);
		try {
			sut.create(creationData);
			fail("should fail on duplicate slug");
		}
		catch( SimpleValidationException expected ) {
			// expected
		}
		verify(authorizer).create(eq(creationData), any());
	}

	@Test
	void testCreate() {
		ArticleCreationData creationData = prepareForCreation();
		when(articleDao.slugExists(SLUG)).thenReturn(false);
		when(articleDao.create(any(ArticleCreationData.class), anyString(), any(LocalDateTime.class))).thenReturn(ARTICLE_ID);
		ArticleBase result = sut.create(creationData);
		assertNotNull(result);
		assertEquals(ARTICLE_ID, result.getId());
		assertEquals(TITLE, result.getTitle());
		assertEquals(SLUG, result.getSlug());
		assertEquals(DESCRIPTION, result.getDescription());
		assertEquals(CREATED_AT, result.getCreatedAt());
		assertNull(result.getUpdatedAt());
		verify(articleDao).create(eq(creationData), eq(SLUG), eq(CREATED_AT));
		verify(authorizer).create(eq(creationData), any());
	}

	@Test
	void testUpdateWithNullData() {
		try {
			sut.update(SLUG, null);
			fail("should fail on null update data");
		}
		catch( NullPointerException expected ) {
			// expected
		}
		verifyNoInteractions(authorizer);
	}

	@Test
	void testUpdate() {
		ArticleUpdateData updateData = mock(ArticleUpdateData.class);
		when(dateTimeService.getNow()).thenReturn(NOW);
		when(articleDao.update(eq(SLUG), any(), eq(NOW))).thenReturn(ARTICLE_ID);
		when(authorizer.update(eq(SLUG), any(), any())).thenAnswer(iom -> ((BiFunction<?,?,?>) iom.getArgument(2)).apply(iom.getArgument(0),iom.getArgument(1)));
		String result = sut.update(SLUG, updateData);
		assertEquals(ARTICLE_ID, result);
		ArgumentCaptor<ArticleUpdateData> captor = ArgumentCaptor.forClass(ArticleUpdateData.class);
		verify(articleDao).update(eq(SLUG), captor.capture(), eq(NOW));
		assertSame(updateData, captor.getValue());
	}

	@Test
	void testDelete() {
		sut.delete(SLUG);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Consumer<String>> delegateCaptor = ArgumentCaptor.forClass(Consumer.class);
		verify(authorizer).delete(eq(SLUG), delegateCaptor.capture());
		delegateCaptor.getValue().accept(SLUG);
		verify(articleDao).delete(SLUG);
	}

	private ArticleCreationData prepareForCreation() {
		when(dateTimeService.getNow()).thenReturn(CREATED_AT);
		when(slugifier.apply(TITLE)).thenReturn(SLUG);
		ArticleCreationData creationData = mock(ArticleCreationData.class, withSettings().lenient());
		when(creationData.getAuthorId()).thenReturn(AUTHOR_ID);
		when(creationData.getBody()).thenReturn(BODY);
		when(creationData.getDescription()).thenReturn(DESCRIPTION);
		when(creationData.getTitle()).thenReturn(TITLE);
		when(authorizer.create(eq(creationData), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		return creationData;
	}

	@Test
	void testFindFullDataBySlug() {
		User u = mock(User.class);
		when(u.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(u);
		when(articleDao.findFullDataBySlug(USER_ID, SLUG)).thenReturn(makeArticleCombinedFullData());
		when(articleDao.findTags(ARTICLE_ID)).thenReturn(TAG_LIST);
		when(authorizer.findFullDataBySlug(eq(SLUG), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		ArticleCombinedFullData res = sut.findFullDataBySlug(SLUG);
		assertNotNull(res);
		assertEquals(ARTICLE_ID, res.getArticle().getId());
		assertEquals(TAG_LIST, res.getTagList());
		verify(articleDao).findFullDataBySlug(USER_ID, SLUG);
		verify(authorizer).findFullDataBySlug(eq(SLUG), any());
	}

	@Test
	void testFindFullDataBySlugForAnonymousUser() {
		when(authenticationContext.getUserPrincipal()).thenReturn(null);
		when(articleDao.findFullDataBySlug(null, SLUG)).thenReturn(makeArticleCombinedFullData());
		when(articleDao.findTags(ARTICLE_ID)).thenReturn(TAG_LIST);
		when(authorizer.findFullDataBySlug(eq(SLUG), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		ArticleCombinedFullData res = sut.findFullDataBySlug(SLUG);
		assertNotNull(res);
		assertEquals(ARTICLE_ID, res.getArticle().getId());
		assertEquals(TAG_LIST, res.getTagList());
		verify(articleDao).findFullDataBySlug(null, SLUG);
		verify(authorizer).findFullDataBySlug(eq(SLUG), any());
	}

	private ArticleCombinedFullData makeArticleCombinedFullData() {
		ArticleCombinedFullData d = new ArticleCombinedFullData();
		d.setArticle(ImmutableArticleBase.builder().id(ARTICLE_ID).createdAt(CREATED_AT).description(DESCRIPTION).slug(SLUG).title(TITLE).updatedAt(UPDATED_AT).build());
		d.setAuthor(new NameAndId(AUTHOR_NAME, AUTHOR_ID));
		return d;
	}

	@Test
	void testFind() {
		User u = mock(User.class);
		when(u.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(u);
		ArticleSearchCriteria criteria = ImmutableArticleSearchCriteria.builder()
				.addAuthors("a")
				.favoritedBy("b")
				.build();
		when(authorizer.find(any(ArticleSearchCriteria.class), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		SearchResult<ArticleSearchResult> daoResult = new SearchResult<>();
		when(articleDao.find(anyString(), any())).thenReturn(daoResult);
		SearchResult<ArticleSearchResult> result = sut.find(criteria);
		assertSame(daoResult, result);
		ArgumentCaptor<ArticleSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);
		verify(articleDao).find(eq(USER_ID), criteriaCaptor.capture());
		ArticleSearchCriteria daoCriteria = criteriaCaptor.getValue();
		assertNotNull(daoCriteria.getLimit());
		assertNotNull(daoCriteria.getOffset());
	}
}
