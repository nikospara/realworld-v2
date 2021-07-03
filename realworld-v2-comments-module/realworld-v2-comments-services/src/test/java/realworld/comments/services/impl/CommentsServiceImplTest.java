package realworld.comments.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static realworld.OrderByDirection.ASC;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.OrderBy;
import realworld.OrderByDirection;
import realworld.Paging;
import realworld.SearchResult;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.comments.dao.CommentsDao;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentCreationData;
import realworld.comments.model.CommentOrderBy;
import realworld.services.DateTimeService;

/**
 * Tests for the {@link CommentsServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class CommentsServiceImplTest {

	private static final String ARTICLE_ID = "article_id";
	private static final String ARTICLE_SLUG = "slug";
	private static final String BODY = "Body";
	private static final LocalDateTime CREATION_DATE = LocalDateTime.now();
	private static final String USER_ID = "user_id";
	private static final String COMMENT_ID = "comment_id";

	@Produces @Mock
	private CommentsServiceAuthorizer authorizer;

	@Produces @Mock
	private DateTimeService dateTimeService;

	@Produces @Mock
	private CommentsDao dao;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private CommentsServiceImpl sut;

	@Test
	void testCreateForCurrentUser() {
		when(authorizer.createForCurrentUser(eq(ARTICLE_SLUG), any(CommentCreationData.class), any())).thenAnswer(iom -> ((BiFunction<?,?,?>) iom.getArgument(2)).apply(iom.getArgument(0), iom.getArgument(1)));
		when(dao.findArticleIdForSlug(ARTICLE_SLUG)).thenReturn(Optional.of(ARTICLE_ID));
		when(dao.create(any())).thenReturn(COMMENT_ID);
		when(dateTimeService.getNow()).thenReturn(CREATION_DATE);
		User mockUser = mock(User.class);
		when(mockUser.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(mockUser);
		CommentCreationData commentCreationData = mock(CommentCreationData.class);
		when(commentCreationData.getBody()).thenReturn(BODY);
		Comment result = sut.createForCurrentUser(ARTICLE_SLUG, commentCreationData);
		assertEquals(COMMENT_ID, result.getId());
		assertEquals(ARTICLE_ID, result.getArticleId());
		assertEquals(BODY, result.getBody());
		assertEquals(USER_ID, result.getAuthorId());
		assertEquals(CREATION_DATE, result.getCreatedAt());
		assertEquals(CREATION_DATE, result.getUpdatedAt());
	}

	@Test
	void testDelete() {
		sut.delete(COMMENT_ID);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Consumer<String>> delegateCaptor = ArgumentCaptor.forClass(Consumer.class);
		verify(authorizer).delete(eq(COMMENT_ID), delegateCaptor.capture());
		delegateCaptor.getValue().accept(COMMENT_ID);
		verify(dao).delete(COMMENT_ID);
	}

	@Test
	void testDeleteAllForArticle() {
		sut.deleteAllForArticle(ARTICLE_ID);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Consumer<String>> delegateCaptor = ArgumentCaptor.forClass(Consumer.class);
		verify(authorizer).deleteAllForArticle(eq(ARTICLE_ID), delegateCaptor.capture());
		delegateCaptor.getValue().accept(ARTICLE_ID);
		verify(dao).deleteAllForArticle(ARTICLE_ID);
	}

	@Test
	void testFindCommentsForArticleNullPaging() {
		SearchResult<Comment> result = executeFind(ARTICLE_SLUG, null, 5);
		assertEquals(5L, result.getCount());
		assertEquals(5, result.getResults().size());
		verify(dao).findCommentsForArticlePaged(any(), any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFindCommentsForArticleNoLimit() {
		SearchResult<Comment> result = executeFind(ARTICLE_SLUG, paging(30, null, CommentOrderBy.CREATION_DATE, ASC), 5);
		assertEquals(5L, result.getCount());
		assertEquals(5, result.getResults().size());
		verify(dao).findCommentsForArticlePaged(any(), any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFindCommentsForArticleResultSizeLessThanLimit() {
		SearchResult<Comment> result = executeFind(ARTICLE_SLUG, paging(40, 20, CommentOrderBy.CREATION_DATE, ASC), 5);
		assertEquals(45L, result.getCount());
		assertEquals(5, result.getResults().size());
		verify(dao).findCommentsForArticlePaged(any(), any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFindCommentsForArticle() {
		when(dao.countCommentsForArticle(ARTICLE_SLUG)).thenReturn(60L);
		SearchResult<Comment> result = executeFind(ARTICLE_SLUG, paging(40, 20, CommentOrderBy.CREATION_DATE, ASC), 20);
		assertEquals(60L, result.getCount());
		assertEquals(20, result.getResults().size());
		verify(dao).findCommentsForArticlePaged(any(), any());
		verifyNoMoreInteractions(dao);
	}

	private SearchResult<Comment> executeFind(String slug, Paging<CommentOrderBy> paging, int numberOfResults) {
		List<Comment> results = new ArrayList<>();
		for( int i=0; i < numberOfResults; i++ ) {
			results.add(mock(Comment.class));
		}
		when(dao.findCommentsForArticlePaged(any(), any())).thenReturn(results);
		when(authorizer.findCommentsForArticle(any(), any(), any())).thenAnswer(iom -> ((BiFunction<?,?,?>) iom.getArgument(2)).apply(iom.getArgument(0), iom.getArgument(1)));
		return sut.findCommentsForArticle(slug, paging);
	}

	private Paging<CommentOrderBy> paging(Integer offset, Integer limit, CommentOrderBy orderByField, OrderByDirection direction) {
		OrderBy<CommentOrderBy> orderBy = (orderByField != null || direction != null) ? new OrderBy<>(orderByField, direction) : null;
		Paging<CommentOrderBy> paging = new Paging<>();
		paging.setOffset(offset);
		paging.setLimit(limit);
		paging.setOrderBy(orderBy);
		return paging;
	}
}
