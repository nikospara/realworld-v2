package realworld.user.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import realworld.EntityDoesNotExistException;
import realworld.SimpleConstraintViolation;
import realworld.SimpleValidationException;
import realworld.user.dao.UserDao;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.services.UserService;

/**
 * Implementation of {@link UserService}.
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
class UserServiceImpl implements UserService {

	private UserDao userDao;

	private PasswordEncrypter encrypter;

	/**
	 * Default constructors for the frameworks.
	 */
	UserServiceImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param userDao The user DAO
	 * @param encrypter The password encrypter
	 */
	@Inject
	public UserServiceImpl(UserDao userDao, PasswordEncrypter encrypter) {
		this.userDao = userDao;
		this.encrypter = encrypter;
	}

	@Override
	public UserData register(@Valid UserRegistrationData registrationData) {
		List<SimpleConstraintViolation> errors = new ArrayList<>();

		if( userDao.usernameExists(registrationData.getUsername()) ) {
			errors.add(new SimpleConstraintViolation("username", "duplicate user name"));
		}
		if( userDao.emailExists(registrationData.getEmail()) ) {
			errors.add(new SimpleConstraintViolation("email", "duplicate email"));
		}

		if( !errors.isEmpty() ) {
			throw new SimpleValidationException(errors);
		}

		ImmutableUserData userData = ImmutableUserData.builder()
				.username(registrationData.getUsername())
				.email(registrationData.getEmail())
				.imageUrl(registrationData.getImageUrl())
				.build();

		return userDao.create(userData, encrypter.apply(registrationData.getPassword()));
	}
}
