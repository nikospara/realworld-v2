package realworld.user.dao;

import realworld.user.model.UserData;

/**
 * DAO interface for the User entity.
 */
public interface UserDao {

	/**
	 * Create a user.
	 *
	 * @param user     The user data
	 * @param password The initial password
	 * @return A user object, could be the same as the input
	 */
	UserData create(UserData user, String password);
}
