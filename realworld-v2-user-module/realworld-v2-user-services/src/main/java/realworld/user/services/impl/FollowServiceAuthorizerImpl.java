package realworld.user.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import realworld.authorization.service.Authorization;

/**
 * Security for the {@link realworld.user.services.FollowService} implementation.
 */
@ApplicationScoped
public class FollowServiceAuthorizerImpl implements FollowServiceAuthorizer {

	private Authorization authorization;

	/**
	 * Constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	FollowServiceAuthorizerImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param authorization The authorization utilities
	 */
	@Inject
	public FollowServiceAuthorizerImpl(Authorization authorization) {
		this.authorization = authorization;
	}

	@Override
	public boolean follows(String followerName, String followedName, BiFunction<String,String,Boolean> delegate) {
		authorization.requireUsername(followerName);
		return delegate.apply(followerName, followedName);
	}

	@Override
	public void follow(String followerName, String followedName, BiConsumer<String,String> delegate) {
		authorization.requireUsername(followerName);
		delegate.accept(followerName, followedName);
	}

	@Override
	public void unfollow(String followerName, String followedName, BiConsumer<String,String> delegate) {
		authorization.requireUsername(followerName);
		delegate.accept(followerName, followedName);
	}

	@Override
	public List<String> findAllFollowed(String username, Function<String,List<String>> delegate) {
		authorization.requireUsername(username);
		return delegate.apply(username);
	}

	@Override
	public Map<String, Boolean> checkAllFollowed(String username, List<String> userNames, BiFunction<String,List<String>,Map<String,Boolean>> delegate) {
		authorization.requireUsername(username);
		return delegate.apply(username, userNames);
	}
}
