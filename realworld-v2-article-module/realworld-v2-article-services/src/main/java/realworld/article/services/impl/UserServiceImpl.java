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
 * Despite the fact that this implementation looks naive - it simply delegates to the DAO, it accomplishes 2 tasks:
 * <ol>
 *     <li>Transaction management</li>
 *     <li>Security management</li>
 * </ol>
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
class UserServiceImpl implements UserService {

	private UserServiceAuthorizer authorizer;

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
	 * @param authorizer The authorizer
	 * @param userDao    The user DAO
	 */
	@Inject
	public UserServiceImpl(UserServiceAuthorizer authorizer, UserDao userDao) {
		this.authorizer = authorizer;
		this.userDao = userDao;
	}

	@Override
	public void add(String id, String username) {
		authorizer.add(id, username, userDao::add);
	}

	@Override
	public void updateUsername(String id, String username) {
		authorizer.updateUsername(id, username, userDao::updateUsername);
	}

	@Override
	public Optional<String> findByUserName(String username) {
		return authorizer.findByUserName(username, userDao::findByUserName);
	}

	@Override
	public Optional<String> findByUserId(String id) {
		return authorizer.findByUserId(id, userDao::findByUserId);
	}
}
