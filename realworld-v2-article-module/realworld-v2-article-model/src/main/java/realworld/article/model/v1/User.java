package realworld.article.model.v1;

import realworld.model.common.v1.UserId;
import realworld.model.common.v1.Username;

/**
 * User entity with enough information to satisfy the needs of the article module.
 */
public interface User {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	UserId getId();

	/**
	 * Get the username of this user.
	 *
	 * @return The username of this user
	 */
	Username getUsername();
}
