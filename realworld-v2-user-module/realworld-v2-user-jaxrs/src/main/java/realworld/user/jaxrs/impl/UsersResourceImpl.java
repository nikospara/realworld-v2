package realworld.user.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import realworld.user.jaxrs.UsersResource;
import realworld.user.model.UserData;
import realworld.user.services.UserService;

/**
 * Implementation of the {@link UsersResource}.
 */
@RequestScoped
public class UsersResourceImpl implements UsersResource {

	@Inject
	UserService userService;

	@Context
	private UriInfo uriInfo;

	@Override
	public UserData get(String username) {
		return userService.findByUserName(username);
	}
}
