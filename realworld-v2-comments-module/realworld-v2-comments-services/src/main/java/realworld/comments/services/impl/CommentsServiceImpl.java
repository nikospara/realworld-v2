package realworld.comments.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import realworld.Paging;
import realworld.SearchResult;
import realworld.authentication.AuthenticationContext;
import realworld.comments.dao.CommentsDao;
import realworld.comments.model.Comment;
import realworld.comments.model.ImmutableComment;
import realworld.comments.model.CommentOrderBy;
import realworld.comments.services.CommentsService;
import realworld.services.DateTimeService;

/**
 * Implementation of the {@link CommentsService}.
 */
@ApplicationScoped
@Transactional
public class CommentsServiceImpl implements CommentsService {

	private CommentsServiceAuthorizer authorizer;

	private DateTimeService dateTimeService;

	private CommentsDao dao;

	private AuthenticationContext authenticationContext;

	/**
	 * Injection constructor.
	 *
	 * @param authorizer            The authorizer
	 * @param dateTimeService       The date and time service
	 * @param dao                   The DAO
	 * @param authenticationContext The authentication context
	 */
	@Inject
	public CommentsServiceImpl(CommentsServiceAuthorizer authorizer, DateTimeService dateTimeService, CommentsDao dao, AuthenticationContext authenticationContext) {
		this.authorizer = authorizer;
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
	public Comment createForCurrentUser(String outerArticleId, String outerBody) {
		return authorizer.createForCurrentUser(outerArticleId, outerBody, (articleId, body) -> {
			Comment comment = ImmutableComment.builder()
					.id(UUID.randomUUID().toString())
					.body(body)
					.createdAt(dateTimeService.getNow())
					.authorId(authenticationContext.getUserPrincipal().getUniqueId())
					.articleId(articleId)
					.build();
			dao.create(comment);
			return comment;
		});
	}

	@Override
	public SearchResult<Comment> findCommentsForArticle(String outerArticleId, Paging<CommentOrderBy> outerPaging) {
		return authorizer.findCommentsForArticle(outerArticleId, outerPaging, (articleId, paging) -> {
			List<Comment> results = dao.findCommentsForArticlePaged(articleId, paging);
			if( paging == null || paging.getLimit() == null ) {
				return new SearchResult<>(results.size(), results);
			}
			else if( results.size() < paging.getLimit() ) {
				return new SearchResult<>((paging.getOffset() != null ? paging.getOffset() : 0) + results.size(), results);
			}
			else {
				return new SearchResult<>(dao.countCommentsForArticle(articleId), results);
			}
		});
	}

	@Override
	public void delete(String outerId) {
		authorizer.delete(outerId, dao::delete);
	}

	@Override
	public void deleteAllForArticle(String outerArticleId) {
		authorizer.deleteAllForArticle(outerArticleId, dao::deleteAllForArticle);
	}
}
