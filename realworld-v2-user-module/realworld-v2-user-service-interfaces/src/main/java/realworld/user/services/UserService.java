package realworld.user.services;

import javax.validation.Valid;

import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;

/**
 * User service.
 */
public interface UserService {

	/**
	 * Register a user.
	 *
	 * @param registrationData User registration data
	 * @return The full user profile - never {@code null}
	 */
	UserData register(@Valid UserRegistrationData registrationData);
}
