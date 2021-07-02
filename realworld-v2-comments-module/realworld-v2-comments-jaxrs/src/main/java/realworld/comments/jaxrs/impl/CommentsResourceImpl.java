package realworld.comments.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import realworld.Paging;
import realworld.SearchResult;
import realworld.comments.jaxrs.CommentsResource;
import realworld.comments.model.Comment;
import realworld.comments.services.CommentsService;

/**
 * Implementation of the {@link CommentsResource}.
 */
@RequestScoped
public class CommentsResourceImpl implements CommentsResource {

	@Inject
	CommentsService commentsService;

	@Override
	public SearchResult<Comment> findForArticle(String slug, Integer limit, Integer offset) {
		return commentsService.findCommentsForArticle(slug, Paging.of(offset, limit, null));
	}
}
