package realworld.user.services;

import javax.validation.Valid;

import realworld.EntityDoesNotExistException;
import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;

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
	UserData register(@Valid UserUpdateData registrationData);

	/**
	 * Find by user name.
	 *
	 * @param username The user name
	 * @return The user
	 * @throws EntityDoesNotExistException If not found, the exception message is the user name
	 */
	UserData findByUserName(String username);

	/**
	 * Update the current user.
	 *
	 * @param userUpdateData User update data
	 */
	void update(@Valid UserUpdateData userUpdateData);
}
