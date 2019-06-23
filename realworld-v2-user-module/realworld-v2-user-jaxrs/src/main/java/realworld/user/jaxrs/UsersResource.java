package realworld.user.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import realworld.user.model.UserData;

/**
 * User operations.
 */
@Path("/users")
@Api(tags=UsersResource.TAG)
public interface UsersResource {

	String TAG = "UsersResource";

	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Returns the profile of the given user.", tags=TAG)
	UserData get(
			@ApiParam(value = "The user name to apply this operation to.", required = true)
			@PathParam("username")
			String username
	);
}
