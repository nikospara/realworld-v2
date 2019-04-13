package realworld.user.services;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import realworld.EntityDoesNotExistException;
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

	/**
	 * Find by user name.
	 *
	 * @param username The user name
	 * @return The user
	 * @throws EntityDoesNotExistException If not found
	 */
	UserData findByUserName(String username);

	/**
	 * Find a user by email (case-insensitive comparison) and password.
	 *
	 * @param email    The email
	 * @param password The encrypted password
	 * @return The user
	 * @throws EntityDoesNotExistException If not found
	 */
	UserData findByEmailAndPassword(
			@NotNull
			@Pattern(regexp="^.+@.+\\.[a-z]+$", flags=CASE_INSENSITIVE)
			String email,
			@NotNull
			@Size(min=5)
			String password
	);
}
