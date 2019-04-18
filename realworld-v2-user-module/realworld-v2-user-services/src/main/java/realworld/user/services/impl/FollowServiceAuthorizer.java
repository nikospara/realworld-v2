package realworld.user.services.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import java.util.List;
import java.util.Map;

import realworld.authorization.service.Authorization;
import realworld.user.services.FollowService;

/**
 * Security for the {@link FollowService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class FollowServiceAuthorizer implements FollowService {

	private FollowService delegate;

	private Authorization authorization;

	/**
	 * Injection constructor.
	 *
	 * @param delegate      The delegate of this decorator
	 * @param authorization The authorization utilities
	 */
	@Inject
	public FollowServiceAuthorizer(@Delegate FollowService delegate, Authorization authorization) {
		this.delegate = delegate;
		this.authorization = authorization;
	}

	@Override
	public boolean follows(String followerName, String followedName) {
		authorization.requireUsername(followerName);
		return delegate.follows(followerName, followedName);
	}

	@Override
	public void follow(String followerName, String followedName) {
		authorization.requireUsername(followerName);
		delegate.follow(followerName, followedName);
	}

	@Override
	public void unfollow(String followerName, String followedName) {
		authorization.requireUsername(followerName);
		delegate.unfollow(followerName, followedName);
	}

	@Override
	public List<String> findAllFollowed(String username) {
		authorization.requireUsername(username);
		return delegate.findAllFollowed(username);
	}

	@Override
	public Map<String, Boolean> checkAllFollowed(String username, List<String> userNames) {
		authorization.requireUsername(username);
		return delegate.checkAllFollowed(username, userNames);
	}
}
