package realworld.user.services.authz.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.authentication.AuthenticationContext;
import realworld.authorization.service.Authorization;
import realworld.user.services.BiographyService;

/**
 * Security for the {@link realworld.user.services.BiographyService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class BiographyServiceAuthorizer implements BiographyService {

	private BiographyService delegate;

	private Authorization authorization;

	private AuthenticationContext authenticationContext;

	/**
	 * Constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	BiographyServiceAuthorizer() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param delegate The delegate of this decorator
	 * @param authorization The authorization
	 */
	@Inject
	public BiographyServiceAuthorizer(@Delegate BiographyService delegate, Authorization authorization, AuthenticationContext authenticationContext) {
		this.delegate = delegate;
		this.authorization = authorization;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public void create(String userId, String content) {
		delegate.create(userId, content);
	}

	@Override
	public String findByUserName(String username) {
		return delegate.findByUserName(username);
	}

	@Override
	public void updateByUserName(String username, String content) {
		authorization.requireUsername(username);
		delegate.updateByUserName(username, content);
	}

	@Override
	public void updateById(String userId, String content) {
		authorization.requireLogin();
		if( !authenticationContext.getUserPrincipal().getUniqueId().equals(userId) ) {
			authorization.requireSystemUser();
		}
		delegate.updateById(userId, content);
	}
}
