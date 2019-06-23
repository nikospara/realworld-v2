package realworld.user.jaxrs;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * User operations.
 */
@Path("/users/{username}/follows")
@Api(tags = FollowsResource.TAG)
public interface FollowsResource {

	String TAG = "FollowsResource";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Check which of the users in the list are followed by the given user.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=200, message="Success")
	)
	Map<String,Boolean> checkFollowed(
			@ApiParam(value = "The user name of the user to test for followed users.", required = true)
			@PathParam("username")
			String username,
			@ApiParam(value = "The user names of the users to test if they are being followed.", required = false)
			@QueryParam("u")
			List<String> usernames
	);

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get the list of all the users that are followed by the given user.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=200, message="Success")
	)
	List<String> findAllFollowed(
			@ApiParam(value = "The user name of the user to test for followed users.", required = true)
			@PathParam("username")
			String username
	);

	@GET
	@Path("/{followedUsername}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Check if a user follows another.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=200, message="Success")
	)
	boolean follows(
			@ApiParam(value = "The user name of the user to follow.", required = true)
			@PathParam("username")
			String username,
			@ApiParam(value = "The user name of the user to be followed.", required = true)
			@PathParam("followedUsername")
			String followedUsername
	);

	@POST
	@Path("/{followedUsername}")
	@ApiOperation(value="Follow user.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=204, message="Successfully followed")
	)
	Response follow(
			@ApiParam(value = "The user name of the user to follow.", required = true)
			@PathParam("username")
			String username,
			@ApiParam(value = "The user name of the user to be followed.", required = true)
			@PathParam("followedUsername")
			String followedUsername
	);

	@DELETE
	@Path("/{followedUsername}")
	@ApiOperation(value="Follow user.", tags=TAG)
	@ApiResponses(
			@ApiResponse(code=204, message="Successfully unfollowed")
	)
	Response unfollow(
			@ApiParam(value = "The user name of the user to unfollow.", required = true)
			@PathParam("username")
			String username,
			@ApiParam(value = "The user name of the user to be unfollowed.", required = true)
			@PathParam("followedUsername")
			String followedUsername
	);
}
