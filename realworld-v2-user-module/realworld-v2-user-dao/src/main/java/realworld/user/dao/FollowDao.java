package realworld.user.dao;

import java.util.List;
import java.util.Map;

/**
 * DAO interface for the Follow entity.
 */
public interface FollowDao {

	/**
	 * Check if there exists a Follow entity linking the user with id {@code followerId}
	 * to the user with id {@code followedId}.
	 * The operation doesn't care if either or both ids or the respective Follow entity
	 * do not exist.
	 *
	 * @param followerId Id of the follower user
	 * @param followedId Id of the followed user
	 */
	boolean exists(String followerId, String followedId);

	/**
	 * Let user with id {@code followerId} follow user with id {@code followedId}.
	 * The ids are assumed to exist, i.e. the caller must have resolved them.
	 *
	 * @param followerId Id of the follower user
	 * @param followedId Id of the followed user
	 */
	void create(String followerId, String followedId);

	/**
	 * Let user with id {@code followerId} unfollow user with id {@code followedId}.
	 * The deletion doesn't care if either or both ids or the respective Follow entity
	 * do not exist.
	 *
	 * @param followerId Id of the follower user
	 * @param followedId Id of the followed user
	 * @return The number of entities deleted
	 */
	int delete(String followerId, String followedId);

	/**
	 * Find the user names of all users followed by the one with the given id.
	 *
	 * @param userId The id of the user
	 * @return A list of all the followed user names
	 */
	List<String> findAllFollowed(String userId);

	/**
	 * Check which of the user names in the given list are followed by the user with the given id.
	 *
	 * @param userId    The id of the user
	 * @param userNames The list of user names to check if they are followed
	 * @return A map of user name to whether he/she is being followed
	 */
	Map<String,Boolean> checkAllFollowed(String userId, List<String> userNames);
}
