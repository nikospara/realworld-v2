package realworld.comments.jaxrs;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
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

	@POST
	@ApiOperation(value="Comment on a specific article.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=201, message="Successfully created", responseHeaders=@ResponseHeader(name="Location", description="The location of the article containing the newly created comment"))
	)
	Response create(
			@ApiParam(value = "The slug of the article to comment.", required = true)
			@PathParam("slug")
			String slug,
			@ApiParam(value = "Content of a comment", required = true)
			CommentCreationParam comment
	);

	@DELETE
	@Path("{id}")
	@ApiOperation(value="Delete comment.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=204, message="Successfully deleted")
	)
	Response delete(
			@ApiParam(value = "The slug of the article containing the comment to delete.", required = true)
			@PathParam("slug")
			String slug,
			@ApiParam(value = "The id of the comment to delete.", required = true)
			@PathParam("id")
			String id
	);
}
