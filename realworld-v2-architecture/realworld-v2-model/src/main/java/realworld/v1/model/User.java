package realworld.v1.model;

import java.net.URL;

import realworld.v1.types.Email;
import realworld.v1.types.StructuredText;
import realworld.v1.types.UserId;
import realworld.v1.types.Username;

/**
 * Representation of a user of the system.
 */
public interface User {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	UserId getId();

	StructuredText getBio();

	Email getEmail();

	Username getUsername();

	URL getImage();
}
