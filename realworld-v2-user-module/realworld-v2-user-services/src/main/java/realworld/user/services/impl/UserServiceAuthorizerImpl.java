package realworld.user.services.impl;

import static realworld.authorization.service.Authorization.REDUCTED;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.function.Function;

import realworld.authentication.AuthenticationContext;
import realworld.authorization.service.Authorization;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;

/**
 * Security for the {@link realworld.user.services.UserService} implementation.
 */
@ApplicationScoped
public class UserServiceAuthorizerImpl implements UserServiceAuthorizer {

	private Authorization authorization;

	private AuthenticationContext authenticationContext;

	/**
	 * Constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	UserServiceAuthorizerImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param authorization The authorization utilities
	 * @param authenticationContext The authentication context
	 */
	@Inject
	public UserServiceAuthorizerImpl(Authorization authorization, AuthenticationContext authenticationContext) {
		this.authorization = authorization;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public UserData register(UserUpdateData registrationData, Function<UserUpdateData, UserData> delegate) {
		return delegate.apply(registrationData);
	}

	@Override
	public UserData findByUserName(String username, Function<String, UserData> delegate) {
		UserData userData = delegate.apply(username);
		if( authenticationContext.getUserPrincipal() == null || !authenticationContext.getUserPrincipal().getUniqueId().equals(userData.getId()) ) {
			userData = ImmutableUserData.builder().from(userData).id(REDUCTED).email(REDUCTED).build();
		}
		return userData;
	}

	@Override
	public void update(UserUpdateData userUpdateData, Consumer<UserUpdateData> delegate) {
		authorization.requireLogin();
		delegate.accept(userUpdateData);
	}
}
