package realworld.user.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.function.BiConsumer;
import java.util.function.Function;

import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.user.services.BiographyService} implementation.
 */
@ApplicationScoped
public class BiographyServiceAuthorizerImpl implements BiographyServiceAuthorizer {

	private Authorization authorization;

	/**
	 * Constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	BiographyServiceAuthorizerImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param authorization The authorization
	 */
	@Inject
	public BiographyServiceAuthorizerImpl(Authorization authorization) {
		this.authorization = authorization;
	}

	@Override
	public void create(String userId, String content, BiConsumer<String,String> delegate) {
		delegate.accept(userId, content);
	}

	@Override
	public String findByUserName(String username, Function<String,String> delegate) {
		return delegate.apply(username);
	}

	@Override
	public void updateByUserName(String username, String content, BiConsumer<String,String> delegate) {
		authorization.requireUsername(username);
		delegate.accept(username, content);
	}

	@Override
	public void updateById(String userId, String content, BiConsumer<String,String> delegate) {
		authorization.requireUserId(userId);
		delegate.accept(userId, content);
	}
}
