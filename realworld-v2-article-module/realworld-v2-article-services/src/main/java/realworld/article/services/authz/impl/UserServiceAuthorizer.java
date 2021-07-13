package realworld.article.services.authz.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import java.util.Optional;

import realworld.article.services.UserService;
import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.article.services.UserService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class UserServiceAuthorizer implements UserService {

	private UserService delegate;

	private Authorization authorization;

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	UserServiceAuthorizer() {
		// NOOP
	}

	/**
	 * Constructor for injection.
	 *
	 * @param delegate The delegate of this decorator
	 * @param authorization The authorization
	 */
	@Inject
	public UserServiceAuthorizer(@Delegate UserService delegate, Authorization authorization) {
		this.delegate = delegate;
		this.authorization = authorization;
	}

	@Override
	public void add(String id, String username) {
		authorization.requireSystemUser();
		delegate.add(id, username);
	}

	@Override
	public void updateUsername(String id, String username) {
		authorization.requireSystemUser();
		delegate.updateUsername(id, username);
	}

	@Override
	public Optional<String> findByUserName(String username) {
		authorization.requireSystemUser();
		return delegate.findByUserName(username);
	}

	@Override
	public Optional<String> findByUserId(String id) {
		authorization.requireSystemUser();
		return delegate.findByUserId(id);
	}
}
