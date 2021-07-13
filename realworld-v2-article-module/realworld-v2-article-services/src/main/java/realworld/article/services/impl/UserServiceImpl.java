package realworld.article.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

import realworld.EntityDoesNotExistException;
import realworld.article.dao.UserDao;
import realworld.article.services.UserService;

/**
 * Implementation of the limited user service for the article module.
 * <p>
 * Despite the fact that this implementation looks naive - it simply delegates to the DAO - it is responsible for 2
 * distinct tasks:
 *
 * <ol>
 *     <li>Transaction management</li>
 *     <li>Security management</li>
 * </ol>
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
class UserServiceImpl implements UserService {

	private UserDao userDao;

	/**
	 * Default constructor for the frameworks.
	 */
	@SuppressWarnings("unused")
	UserServiceImpl() {
		// NOOP
	}

	/**
	 * Injection constructor.
	 *
	 * @param userDao    The user DAO
	 */
	@Inject
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void add(String id, String username) {
		userDao.add(id, username);
	}

	@Override
	public void updateUsername(String id, String username) {
		userDao.updateUsername(id, username);
	}

	@Override
	public Optional<String> findByUserName(String username) {
		return userDao.findByUserName(username);
	}

	@Override
	public Optional<String> findByUserId(String id) {
		return userDao.findByUserId(id);
	}
}
