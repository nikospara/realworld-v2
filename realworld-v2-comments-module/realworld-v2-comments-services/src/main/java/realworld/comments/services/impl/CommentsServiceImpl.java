package realworld.comments.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import realworld.EntityDoesNotExistException;
import realworld.Paging;
import realworld.SearchResult;
import realworld.authentication.AuthenticationContext;
import realworld.comments.dao.CommentsDao;
import realworld.comments.model.Comment;
import realworld.comments.model.CommentCreationData;
import realworld.comments.model.CommentOrderBy;
import realworld.comments.model.ImmutableComment;
import realworld.comments.services.CommentsService;
import realworld.services.DateTimeService;

/**
 * Implementation of the {@link CommentsService}.
 */
@ApplicationScoped
@Transactional
public class CommentsServiceImpl implements CommentsService {

	private DateTimeService dateTimeService;

	private CommentsDao dao;

	private AuthenticationContext authenticationContext;

	/**
	 * Injection constructor.
	 *
	 * @param dateTimeService       The date and time service
	 * @param dao                   The DAO
	 * @param authenticationContext The authentication context
	 */
	@Inject
	public CommentsServiceImpl(DateTimeService dateTimeService, CommentsDao dao, AuthenticationContext authenticationContext) {
		this.dateTimeService = dateTimeService;
		this.dao = dao;
		this.authenticationContext = authenticationContext;
	}

	/**
	 * Default constructor, used by frameworks.
	 */
	@SuppressWarnings("unused")
	CommentsServiceImpl() {
		// NOOP
	}

	@Override
	public Comment createForCurrentUser(String slug, CommentCreationData commentCreationData) {
		LocalDateTime now = dateTimeService.getNow();
		ImmutableComment comment = dao.findArticleIdForSlug(slug)
				.map(articleId -> ImmutableComment.builder()
						.body(commentCreationData.getBody())
						.createdAt(now)
						.updatedAt(now)
						.authorId(authenticationContext.getUserPrincipal().getUniqueId())
						.articleId(articleId)
						.build()
				)
				.orElseThrow(() -> new EntityDoesNotExistException("article with slug: " + slug));
		String id = dao.create(comment);
		return comment.withId(id);
	}

	@Override
	public SearchResult<Comment> findCommentsForArticle(String slug, Paging<CommentOrderBy> paging) {
		List<Comment> results = dao.findCommentsForArticlePaged(slug, paging);
		if( paging == null || paging.getLimit() == null ) {
			return new SearchResult<>(results.size(), results);
		}
		else if( results.size() < paging.getLimit() ) {
			return new SearchResult<>((paging.getOffset() != null ? paging.getOffset() : 0) + results.size(), results);
		}
		else {
			return new SearchResult<>(dao.countCommentsForArticle(slug), results);
		}
	}

	@Override
	public void delete(String id) {
		dao.delete(id);
	}

	@Override
	public void deleteAllForArticle(String articleId) {
		dao.deleteAllForArticle(articleId);
	}
}
