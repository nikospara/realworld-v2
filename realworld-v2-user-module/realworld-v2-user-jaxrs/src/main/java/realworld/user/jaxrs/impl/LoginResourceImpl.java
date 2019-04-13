package realworld.user.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import realworld.EntityDoesNotExistException;
import realworld.authorization.NotAuthenticatedException;
import realworld.jaxrs.sys.authentication.JwtService;
import realworld.user.jaxrs.LoginParam;
import realworld.user.jaxrs.LoginResource;
import realworld.user.model.UserData;
import realworld.user.services.UserService;

/**
 * Implementation of the {@link LoginResource}.
 */
@RequestScoped
public class LoginResourceImpl implements LoginResource {

	@Inject
	private UserService userService;

	@Inject
	private JwtService jwtService;

	@Override
	public String login(LoginParam loginParam) {
		try {
			UserData userData = userService.findByEmailAndPassword(loginParam.getEmail(), loginParam.getPassword());
			return jwtService.toToken(userData.getId(), userData.getUsername());
		}
		catch( EntityDoesNotExistException e ) {
			throw new NotAuthenticatedException();
		}
	}
}
