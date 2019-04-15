package realworld.user.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.model.UserUpdateData;

/**
 * User operations.
 */
@Path("/users")
@Api(tags=UsersResource.TAG)
public interface UsersResource {

	String TAG = "UsersResource";

	@POST
	@ApiOperation(value="Register user.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=201, message="Successfully created", responseHeaders=@ResponseHeader(name="Location", description="The location of the newly created user"))
	)
	Response register(
			@ApiParam(value = "Information required to register.", required = true)
			UserRegistrationData registerParam
	);

	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Returns the profile of the given user.", tags=TAG)
	UserData get(
			@ApiParam(value = "The user name to apply this operation to.", required = true)
			@PathParam("username")
			String username
	);

	@PUT
	@Path("/{username}")
	@ApiOperation(value="Updates the current user.", tags=TAG)
	Response update(
			@ApiParam(value = "Information to update.", required = true)
			UserUpdateData updateParam
	);
}
