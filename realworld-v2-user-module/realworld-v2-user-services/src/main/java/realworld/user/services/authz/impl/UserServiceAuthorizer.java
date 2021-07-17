package realworld.user.services.authz.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;
import static realworld.authorization.service.Authorization.REDUCTED;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.authentication.AuthenticationContext;
import realworld.authorization.service.Authorization;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;
import realworld.user.services.UserService;

/**
 * Security for the {@link realworld.user.services.UserService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class UserServiceAuthorizer implements UserService {

	private UserService delegate;

	private Authorization authorization;

	private AuthenticationContext authenticationContext;

	/**
	 * Constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	UserServiceAuthorizer() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param delegate The delegate of this decorator
	 * @param authorization The authorization utilities
	 * @param authenticationContext The authentication context
	 */
	@Inject
	public UserServiceAuthorizer(@Delegate UserService delegate, Authorization authorization, AuthenticationContext authenticationContext) {
		this.delegate = delegate;
		this.authorization = authorization;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public UserData register(UserUpdateData registrationData) {
		return delegate.register(registrationData);
	}

	@Override
	public UserData findByUserName(String username) {
		UserData userData = delegate.findByUserName(username);
		if( authenticationContext.getUserPrincipal() == null || !authenticationContext.getUserPrincipal().getUniqueId().equals(userData.getId()) ) {
			userData = ImmutableUserData.builder().from(userData).id(REDUCTED).email(REDUCTED).build();
		}
		return userData;
	}

	@Override
	public void update(UserUpdateData userUpdateData) {
		authorization.requireLogin();
		if( !authenticationContext.getUserPrincipal().getUniqueId().equals(userUpdateData.getId()) ) {
			authorization.requireSystemUser();
		}
		delegate.update(userUpdateData);
	}
}
