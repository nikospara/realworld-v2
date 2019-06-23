package realworld.user.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Biography operations.
 */
@Path("/users/{username}/bio")
@Api(tags=UsersResource.TAG)
public interface BiographyResource {

	String TAG = "BiographyResource";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Returns a string, the user's biography.", tags=TAG)
	@ApiResponses({
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 404, message = "User not found")
	})
	String get(
			@ApiParam(value = "The user name to apply this operation to.", required = true)
			@PathParam("username")
			String username
	);
}
