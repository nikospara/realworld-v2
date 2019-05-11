package realworld.article.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import realworld.article.model.ArticleCombinedFullData;

/**
 * Article operations.
 */
@Path("/articles")
@Api(tags=ArticlesResource.TAG)
public interface ArticlesResource {

	String TAG = "ArticlesResource";

	@GET
	@Path("/{slug}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Returns the profile of the given user.", tags=TAG)
	ArticleCombinedFullData get(
			@ApiParam(value = "The slug of the article to get.", required = true)
			@PathParam("slug")
			String slug
	);
}
