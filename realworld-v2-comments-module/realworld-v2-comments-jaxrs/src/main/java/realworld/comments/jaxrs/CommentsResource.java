package realworld.comments.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import realworld.SearchResult;
import realworld.comments.model.Comment;

/**
 * Comments endpoints.
 */
@Path("/articles/{slug}/comments")
@Api(tags = CommentsResource.TAG)
public interface CommentsResource {

	String TAG = "CommentsResource";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Searches for articles.", tags=TAG)
	SearchResult<Comment> findForArticle(
			@ApiParam(value = "The slug of the article whose comments to get.", required = true)
			@PathParam("slug")
			String slug,
			@ApiParam(value = "Limit returned results.")
			@QueryParam("limit")
			Integer limit,
			@ApiParam(value = "Offset/skip number of articles.")
			@QueryParam("offset")
			Integer offset
	);
}
