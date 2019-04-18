package realworld.user.services;

import java.util.List;
import java.util.Map;

/**
 * Follow service.
 */
public interface FollowService {

	/**
	 * Check if the user with username {@code followerName} follows user with username {@code followedName}.
	 *
	 * @param followerName Username of the follower user
	 * @param followedName Username of the followed user
	 */
	boolean follows(String followerName, String followedName);

	/**
	 * Let user with username {@code followerName} follow user with username {@code followedName}.
	 *
	 * @param followerName Username of the follower user
	 * @param followedName Username of the followed user
	 */
	void follow(String followerName, String followedName);

	/**
	 * Let user with username {@code followerName} unfollow user with username {@code followedName}.
	 *
	 * @param followerName Username of the follower user
	 * @param followedName Username of the followed user
	 */
	void unfollow(String followerName, String followedName);

	/**
	 * Find the user names of all users followed by the one with the given user name.
	 *
	 * @param username The user name
	 * @return A list of all the followed user names
	 */
	List<String> findAllFollowed(String username);

	/**
	 * Check which of the user names in the given list are followed by the user with the given user name.
	 *
	 * @param username  The user name
	 * @param userNames The list of user names to check if they are followed
	 * @return A map of user name to whether he/she is being followed
	 */
	Map<String,Boolean> checkAllFollowed(String username, List<String> userNames);
}
