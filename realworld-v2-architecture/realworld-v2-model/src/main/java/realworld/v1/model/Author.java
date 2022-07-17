package realworld.v1.model;

import java.net.URL;
import java.util.Optional;

import realworld.v1.types.AuthorId;
import realworld.v1.types.PersonName;
import realworld.v1.types.StructuredText;

/**
 * The author of an article, not necessarily a user of the system.
 */
public interface Author {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	AuthorId getId();

	Optional<PersonName> getName();

	StructuredText getBio();

	URL getImage();
}
