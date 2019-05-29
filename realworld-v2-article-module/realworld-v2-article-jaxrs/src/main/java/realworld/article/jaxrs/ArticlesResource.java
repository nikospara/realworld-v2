package realworld.article.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

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

	@GET
	@Path("/{slug}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Returns the profile of the given user.", tags=TAG)
	ArticleCombinedFullDataDto get(
			@ApiParam(value = "The slug of the article to get.", required = true)
			@PathParam("slug")
			String slug
	);
}
