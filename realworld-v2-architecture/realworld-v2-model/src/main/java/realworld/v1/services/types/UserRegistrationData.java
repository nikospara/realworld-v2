package realworld.v1.services.types;

import java.net.URL;

import realworld.v1.types.StructuredText;
import realworld.v1.types.Username;

public interface UserRegistrationData {

	StructuredText getBio();

	Username getUsername();

	URL getImage();
}
