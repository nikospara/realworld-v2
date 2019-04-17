package realworld.user.dao;

import java.util.Optional;

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

	/**
	 * Check if the given user name exists in the DB.
	 *
	 * @param username The user name to check
	 * @return Whether the user name exists
	 */
	boolean usernameExists(String username);

	/**
	 * Check if the given email exists in the DB. Comparison is case-insensitive.
	 *
	 * @param email The email to check
	 * @return Whether the email exists
	 */
	boolean emailExists(String email);

	/**
	 * Find a user by email (case-insensitive comparison) and password.
	 *
	 * @param email    The email
	 * @param password The encrypted password
	 * @return The user object
	 */
	Optional<UserData> findByEmailAndPassword(String email, String password);

	/**
	 * Find user by user name.
	 *
	 * @param username The user name
	 * @return The user with the given user name
	 */
	Optional<UserData> findByUserName(String username);

	/**
	 * Create a User updateById operation. This operation is bound to the current transaction.
	 *
	 * @return The User updateById operation
	 */
	UserUpdateOperation createUpdate();
}
