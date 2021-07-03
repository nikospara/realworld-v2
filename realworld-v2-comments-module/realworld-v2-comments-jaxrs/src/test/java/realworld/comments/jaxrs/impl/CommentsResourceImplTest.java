package realworld.comments.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static realworld.comments.jaxrs.impl.CommentsSearchResultsDtoAssertions.commentsSearchResultsDto;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.UUID;

import io.smallrye.config.inject.ConfigExtension;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.SearchResult;
import realworld.comments.jaxrs.CommentsResource;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentCreationData;
import realworld.comments.model.ImmutableComment;
import realworld.comments.services.CommentsService;
import realworld.jaxrs.sys.ObjectMapperProvider;
import realworld.jaxrs.sys.exceptionmap.EntityDoesNotExistExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;

/**
 * Tests for the {@link CommentsResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions({ResteasyCdiExtension.class, ConfigExtension.class})
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class CommentsResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";
	private static final String SLUG = "slug";
	private static final String ARTICLE_ID = UUID.randomUUID().toString();
	private static final String COMMENT_ID = UUID.randomUUID().toString();
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final String BODY = "The content Body";
	private static final LocalDateTime CREATED_AT = LocalDateTime.of(2019, Month.MARCH, 11, 16, 45, 55);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2019, Month.MARCH, 12, 16, 45, 55);

	@Produces @Mock
	private CommentsService commentsService;

	@Inject
	private CommentsResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(ObjectMapperProvider.class, EntityDoesNotExistExceptionMapper.class);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(CommentsResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testFindForArticle() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/articles/" + SLUG + "/comments")
				.accept(MediaType.APPLICATION_JSON);

		Comment comment = stockComment();
		SearchResult<Comment> result = new SearchResult<>(1L, Collections.singletonList(comment));
		when(commentsService.findCommentsForArticle(eq(SLUG), any())).thenReturn(result);

		dispatcher.invoke(request, response);

		commentsSearchResultsDto(response)
				.assertCount(1)
				.nextResult()
				.assertId(COMMENT_ID)
				.assertBody(BODY)
				.assertCreatedAt("2019-03-11T16:45:55.000Z")
				.assertUpdatedAt("2019-03-12T16:45:55.000Z")
				.assertAuthorId(AUTHOR_ID)
				.assertArticleId(ARTICLE_ID);
	}

	@Test
	void testCreate() throws Exception {
		MockHttpRequest request = MockHttpRequest.post(APPLICATION_PATH + "/articles/" + SLUG + "/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(("{\"body\": \"" + BODY + "\"}").getBytes());

		when(commentsService.createForCurrentUser(eq(SLUG), any(CommentCreationData.class))).thenReturn(stockComment());

		dispatcher.invoke(request, response);

		assertEquals(201, response.getStatus());
	}

	private Comment stockComment() {
		return ImmutableComment.builder()
				.id(COMMENT_ID)
				.articleId(ARTICLE_ID)
				.authorId(AUTHOR_ID)
				.body(BODY)
				.createdAt(CREATED_AT)
				.updatedAt(UPDATED_AT)
				.build();
	}
}
