package realworld.user.services;

import realworld.EntityDoesNotExistException;

/**
 * User service.
 */
public interface BiographyService {

	/**
	 * Create the biography entry for the user with the given id.
	 *
	 * @param userId  The user id
	 * @param content The biography content
	 */
	void create(String userId, String content);

	/**
	 * Find the biography of the user with the given user name.
	 *
	 * @param username The user name
	 * @return The biography
	 * @throws EntityDoesNotExistException If not found
	 */
	String findByUserName(String username);

	/**
	 * Update the biography entry for the user with the given user name.
	 *
	 * @param username The user name
	 * @param content  The biography content
	 */
	void updateByUserName(String username, String content);

	/**
	 * Update the biography entry for the user with the given id.
	 *
	 * @param userId  The user id
	 * @param content The biography content
	 */
	void updateById(String userId, String content);
}
