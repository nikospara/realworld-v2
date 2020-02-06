package realworld.article.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

/**
 * Article operations.
 */
@Path("/articles")
@Api(tags=ArticlesResource.TAG)
public interface ArticlesResource {

	String TAG = "ArticlesResource";

	@POST
	@ApiOperation(value="Create article.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=201, message="Successfully created", responseHeaders=@ResponseHeader(name="Location", description="The location of the newly created article"))
	)
	Response create(
			@ApiParam(value = "Information required to create an article.", required = true)
			ArticleCreationParam creationParam
	);

	@PUT
	@Path("/{slug}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update article.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=204, message="Successfully updated", responseHeaders=@ResponseHeader(name="Location", description="The location of the newly created article"))
	)
	Response update(
			@ApiParam(value = "The slug of the article to put.", required = true)
			@PathParam("slug")
					String slug,
			@ApiParam(value = "Information required to update an article.", required = true)
					ArticleUpdateParam updateParam
	);

	@GET
	@Path("/{slug}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Returns the profile of the given user.", tags=TAG)
	ArticleCombinedFullDataDto get(
			@ApiParam(value = "The slug of the article to get.", required = true)
			@PathParam("slug")
			String slug
	);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Searches for articles.", tags=TAG)
	SearchResult<ArticleSearchResultDto> find(
			@ApiParam(value = "Filter by tag.")
			@QueryParam("tag")
			String tag,
			@ApiParam(value = "Filter by author.")
			@QueryParam("author")
			String author,
			@ApiParam(value = "Favorited by user.")
			@QueryParam("favorited")
			String favoritedBy,
			@ApiParam(value = "Limit returned results.")
			@QueryParam("limit")
			Integer limit,
			@ApiParam(value = "Offset/skip number of articles.")
			@QueryParam("offset")
			Integer offset
	);
}
