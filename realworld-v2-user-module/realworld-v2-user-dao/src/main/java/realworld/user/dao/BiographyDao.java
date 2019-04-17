package realworld.user.dao;

import java.util.Optional;
import realworld.EntityDoesNotExistException;

/**
 * DAO interface for the Biography entity.
 */
public interface BiographyDao {

	/**
	 * Create the biography entry for the user with the given id.
	 *
	 * @param userId  The user id
	 * @param content The biography content
	 */
	void create(String userId, String content);

	/**
	 * Update the biography entry for the user with the given id.
	 *
	 * @param userId  The user id
	 * @param content The biography content
	 */
	void updateById(String userId, String content);

	/**
	 * Update the biography entry for the user with the given user name.
	 *
	 * @param username The user name
	 * @param content  The biography content
	 * @throws EntityDoesNotExistException If not found
	 */
	void updateByUserName(String username, String content);

	/**
	 * Find the biography of the user with the given user name.
	 *
	 * @param username The user name
	 * @return The biography
	 */
	Optional<String> findByUserName(String username);
}
