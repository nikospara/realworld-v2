package realworld.article.model.v1;

import java.util.Optional;

import realworld.model.common.v1.Token;

/**
 * Representation of a connected, possibly authenticated user.
 */
public interface AuthenticatedUser extends User {
	/**
	 * Get the authentication token related to the current user.
	 *
	 * @return The authentication token related to the current user
	 */
	Optional<Token> getToken();

	/**
	 * Whether this user is object represents an authenticated or the anonymous user.
	 *
	 * @return Whether this is an authenticated or the anonymous user
	 */
	boolean isAuthenticated();
}
