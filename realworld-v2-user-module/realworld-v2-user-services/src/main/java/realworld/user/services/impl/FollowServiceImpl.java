package realworld.user.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Map;

import realworld.EntityDoesNotExistException;
import realworld.user.dao.FollowDao;
import realworld.user.model.UserData;
import realworld.user.services.FollowService;
import realworld.user.services.UserService;

/**
 * Follow service implementation.
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
public class FollowServiceImpl implements FollowService {

	private FollowDao followDao;

	private UserService userService;

	/**
	 * Default constructor to appease the frameworks.
	 */
	FollowServiceImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param followDao   The Follow entity DAO
	 * @param userService The user service
	 */
	@Inject
	public FollowServiceImpl(FollowDao followDao, UserService userService) {
		this.followDao = followDao;
		this.userService = userService;
	}

	@Override
	public boolean follows(String followerName, String followedName) {
		UserData follower = userService.findByUserName(followerName);
		UserData followed = userService.findByUserName(followedName);
		return followDao.exists(follower.getId(), followed.getId());
	}

	@Override
	public void follow(String followerName, String followedName) {
		UserData follower = userService.findByUserName(followerName);
		UserData followed = userService.findByUserName(followedName);
		followDao.create(follower.getId(), followed.getId());
	}

	@Override
	public void unfollow(String followerName, String followedName) {
		UserData follower = userService.findByUserName(followerName);
		UserData followed = userService.findByUserName(followedName);
		followDao.delete(follower.getId(), followed.getId());
	}

	@Override
	public List<String> findAllFollowed(String username) {
		UserData follower = userService.findByUserName(username);
		return followDao.findAllFollowed(follower.getId());
	}

	@Override
	public Map<String, Boolean> checkAllFollowed(String username, List<String> userNames) {
		UserData follower = userService.findByUserName(username);
		return followDao.checkAllFollowed(follower.getId(), userNames);
	}
}
