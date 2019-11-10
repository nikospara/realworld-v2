package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.article.services.UserService} implementation.
 */
@ApplicationScoped
public class UserServiceAuthorizerImpl implements UserServiceAuthorizer {

	private Authorization authorization;

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	UserServiceAuthorizerImpl() {
		// NOOP
	}

	/**
	 * Constructor for injection.
	 *
	 * @param authorization The authorization
	 */
	@Inject
	public UserServiceAuthorizerImpl(Authorization authorization) {
		this.authorization = authorization;
	}

	@Override
	public void add(String id, String username, BiConsumer<String, String> delegate) {
		authorization.requireSystemUser();
		delegate.accept(id, username);
	}

	@Override
	public void updateUsername(String id, String username, BiConsumer<String, String> delegate) {
		authorization.requireSystemUser();
		delegate.accept(id, username);
	}

	@Override
	public Optional<String> findByUserName(String username, Function<String, Optional<String>> delegate) {
		authorization.requireSystemUser();
		return delegate.apply(username);
	}

	@Override
	public Optional<String> findByUserId(String id, Function<String, Optional<String>> delegate) {
		authorization.requireSystemUser();
		return delegate.apply(id);
	}
}
