package realworld.user.services.impl;

import static realworld.user.model.UserUpdateData.PropName.BIO;
import static realworld.user.model.UserUpdateData.PropName.EMAIL;
import static realworld.user.model.UserUpdateData.PropName.IMAGE_URL;
import static realworld.user.model.UserUpdateData.PropName.USERNAME;

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
import realworld.user.model.UserUpdateData;
import realworld.user.services.BiographyService;
import realworld.user.services.UserService;

/**
 * Implementation of {@link UserService}.
 */
@ApplicationScoped
@Transactional(dontRollbackOn = EntityDoesNotExistException.class)
class UserServiceImpl implements UserService {

	private UserDao userDao;

	private BiographyService biographyService;

	/**
	 * Default constructors for the frameworks.
	 */
	UserServiceImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param userDao          The user DAO
	 * @param biographyService The biography DAO
	 */
	@Inject
	public UserServiceImpl(UserDao userDao, BiographyService biographyService) {
		this.userDao = userDao;
		this.biographyService = biographyService;
	}

	@Override
	public UserData register(@Valid UserUpdateData registrationData) {
		List<SimpleConstraintViolation> errors = new ArrayList<>();

		if (userDao.usernameExists(registrationData.getUsername())) {
			errors.add(new SimpleConstraintViolation("username", "duplicate user name"));
		}
		if (userDao.emailExists(registrationData.getEmail())) {
			errors.add(new SimpleConstraintViolation("email", "duplicate email"));
		}

		if (!errors.isEmpty()) {
			throw new SimpleValidationException(errors);
		}

		ImmutableUserData userData = ImmutableUserData.builder()
				.id(registrationData.getId())
				.username(registrationData.getUsername())
				.email(registrationData.getEmail())
				.imageUrl(registrationData.getImageUrl())
				.build();

		UserData createdUserData = userDao.create(userData);
		biographyService.create(createdUserData.getId(), registrationData.getBio());

		return createdUserData;
	}

	@Override
	public UserData findByUserName(String username) {
		return userDao.findByUserName(username).orElseThrow(() -> new EntityDoesNotExistException(username));
	}

	@Override
	public void update(@Valid UserUpdateData userUpdateData) {
		UserData u = userDao.findByUserId(userUpdateData.getId()).orElseThrow(EntityDoesNotExistException::new);

		List<SimpleConstraintViolation> errors = new ArrayList<>();

		if( userUpdateData.isExplicitlySet(USERNAME) && !userUpdateData.getUsername().equals(u.getUsername()) && userDao.usernameExists(userUpdateData.getUsername()) ) {
			errors.add(new SimpleConstraintViolation("username", "duplicate user name"));
		}
		if( userUpdateData.isExplicitlySet(EMAIL) && !userUpdateData.getEmail().equals(u.getEmail()) && userDao.emailExists(userUpdateData.getEmail()) ) {
			errors.add(new SimpleConstraintViolation("email", "duplicate email"));
		}

		if( !errors.isEmpty() ) {
			throw new SimpleValidationException(errors);
		}

		userDao.createUpdate()
				.setUsername(userUpdateData.isExplicitlySet(USERNAME), userUpdateData.getUsername())
				.setEmail(userUpdateData.isExplicitlySet(EMAIL), userUpdateData.getEmail())
				.setImageUrl(userUpdateData.isExplicitlySet(IMAGE_URL), userUpdateData.getImageUrl())
				.executeForId(u.getId());

		if( userUpdateData.isExplicitlySet(BIO) ) {
			biographyService.updateById(u.getId(), userUpdateData.getBio());
		}
	}
}
