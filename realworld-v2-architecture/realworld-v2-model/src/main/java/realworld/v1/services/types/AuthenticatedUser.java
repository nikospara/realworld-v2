package realworld.v1.services.types;

import realworld.v1.model.User;

/**
 * Representation of an authenticated user.
 */
public interface AuthenticatedUser extends User {
	Token getToken();
}
