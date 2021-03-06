package realworld.user.services.impl;

import static realworld.user.model.UserUpdateData.PropName.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import realworld.EntityDoesNotExistException;
import realworld.SimpleConstraintViolation;
import realworld.SimpleValidationException;
import realworld.authentication.AuthenticationContext;
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

	private UserServiceAuthorizer authorizer;

	private UserDao userDao;

	private BiographyService biographyService;

	private AuthenticationContext authenticationContext;

	/**
	 * Default constructors for the frameworks.
	 */
	UserServiceImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param authorizer       The authorizer
	 * @param userDao          The user DAO
	 * @param biographyService The biography DAO
	 * @param authenticationContext   The authentication context
	 */
	@Inject
	public UserServiceImpl(UserServiceAuthorizer authorizer, UserDao userDao, BiographyService biographyService, AuthenticationContext authenticationContext) {
		this.authorizer = authorizer;
		this.userDao = userDao;
		this.biographyService = biographyService;
		this.authenticationContext = authenticationContext;
	}

	@Override
	public UserData register(@Valid UserUpdateData outerRegistrationData) {
		return authorizer.register(outerRegistrationData, registrationData -> {
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
		});
	}
	@Override
	public UserData findByUserName(String outerUsername) {
		return authorizer.findByUserName(outerUsername, username -> userDao.findByUserName(username).orElseThrow(() -> new EntityDoesNotExistException(username)));
	}

	@Override
	public void update(@Valid UserUpdateData outerUserUpdateData) {
		authorizer.update(outerUserUpdateData, userUpdateData -> {
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
		});
	}
}
