package realworld.user.jaxrs;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/login")
@Api(tags = LoginResource.TAG)
public interface LoginResource {

	String TAG = "LoginResource";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Login and return the token.", tags=TAG)
	String login(
			@ApiParam(value = "Information required to log in.", required = true)
			LoginParam loginParam
	);
}
