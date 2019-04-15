package realworld.user.jaxrs;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/login")
@Api(tags = LoginResource.TAG)
public interface LoginResource {

	String TAG = "LoginResource";

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Login and return the token.", tags=TAG)
	@ApiResponses({
			@ApiResponse(code = 200, message = "Successfully logged-in"),
			@ApiResponse(code = 401, message = "Login failed")
	})
	String login(
			@ApiParam(value = "Information required to log in.", required = true)
			LoginParam loginParam
	);
}
