package realworld.article.services;

import java.util.Optional;

/**
 * The limited user service for the article module.
 */
public interface UserService {
	/**
	 * Add a username-user id mapping.
	 *
	 * @param id       The user id
	 * @param username The user name
	 */
	void add(String id, String username);

	/**
	 * Update a username-user id mapping.
	 *
	 * @param id       The user id
	 * @param username The user name
	 */
	void updateUsername(String id, String username);

	/**
	 * Find a user id by user name.
	 *
	 * @param username The user name
	 * @return The user id or an empty {@code Optional}
	 */
	Optional<String> findByUserName(String username);

	/**
	 * Find a user name by user id.
	 *
	 * @param id The user id
	 * @return The user name or an empty {@code Optional}
	 */
	Optional<String> findByUserId(String id);
}
