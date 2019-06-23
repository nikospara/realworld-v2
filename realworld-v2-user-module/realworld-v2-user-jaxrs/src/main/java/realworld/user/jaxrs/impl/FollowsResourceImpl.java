package realworld.user.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import realworld.user.jaxrs.FollowsResource;
import realworld.user.services.FollowService;

/**
 * Implementation of the {@link FollowsResource}.
 */
@RequestScoped
public class FollowsResourceImpl implements FollowsResource {

	@Inject
	private FollowService followService;

	@Override
	public Map<String, Boolean> checkFollowed(String username, List<String> usernames) {
		return followService.checkAllFollowed(username, usernames);
	}

	@Override
	public List<String> findAllFollowed(String username) {
		return followService.findAllFollowed(username);
	}

	@Override
	public boolean follows(String username, String followedUsername) {
		return followService.follows(username, followedUsername);
	}

	@Override
	public Response follow(String username, String followedUsername) {
		followService.follow(username, followedUsername);
		return Response.noContent().build();
	}

	@Override
	public Response unfollow(String username, String followedUsername) {
		followService.unfollow(username, followedUsername);
		return Response.noContent().build();
	}
}
