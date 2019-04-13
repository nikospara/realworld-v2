package realworld.user.services.impl;

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
import realworld.user.model.UserRegistrationData;
import realworld.user.services.UserService;

/**
 * Security for the {@link UserService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class UserServiceAuthorizer implements UserService {

	private UserService delegate;

	private Authorization authorization;

	private AuthenticationContext authenticationContext;

	/**
	 * Injection constructor.
	 *
	 * @param delegate      The delegate of this decorator
	 * @param authorization The authorization utilities
	 */
	@Inject
	public UserServiceAuthorizer(@Delegate UserService delegate, Authorization authorization, AuthenticationContext authenticationContext) {
		this.delegate = delegate;
		this.authorization = authorization;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public UserData register(UserRegistrationData registrationData) {
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
	public UserData findByEmailAndPassword(String email, String password) {
		return delegate.findByEmailAndPassword(email, password);
	}
}
