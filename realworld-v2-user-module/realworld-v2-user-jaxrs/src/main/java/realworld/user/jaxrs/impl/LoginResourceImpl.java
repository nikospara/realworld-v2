package realworld.user.jaxrs.impl;

import javax.enterprise.context.RequestScoped;

import realworld.user.jaxrs.LoginParam;
import realworld.user.jaxrs.LoginResource;

/**
 * Implementation of the {@link LoginResource}.
 */
@RequestScoped
public class LoginResourceImpl implements LoginResource {
	@Override
	public String login(LoginParam loginParam) {
		return null;
	}
}
