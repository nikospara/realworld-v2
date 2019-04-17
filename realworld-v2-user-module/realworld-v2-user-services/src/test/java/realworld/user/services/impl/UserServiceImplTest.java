package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.EntityDoesNotExistException;
import realworld.SimpleValidationException;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.user.dao.UserDao;
import realworld.user.dao.UserUpdateOperation;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.model.UserUpdateData;
import realworld.user.model.UserUpdateData.PropName;
import realworld.user.services.BiographyService;

/**
 * Tests for the {@link UserServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	private static final String USERID1 = "USERID1";
	private static final String USERID2 = "USERID2";
	private static final String USERNAME1 = "USERNAME1";
	private static final String USERNAME2 = "USERNAME2";
	private static final String EMAIL1 = "email.one@here.com";
	private static final String EMAIL2 = "email.two@here.com";
	private static final String PASSWORD1 = "PASSWORD1";
	private static final String PASSWORD2 = "PASSWORD2";
	private static final String BIO1 = "BIO1";
	private static final String BIO2 = "BIO2";
	private static final String IMAGE_URL1 = "IMAGE1";
	private static final String IMAGE_URL2 = "IMAGE2";
	private static final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";

	@Produces @Mock
	private UserDao userDao;

	@Produces @Mock
	private BiographyService biographyService;

	@Produces @Mock(lenient = true)
	private PasswordEncrypter encrypter;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private UserServiceImpl sut;

	@BeforeEach
	void init() {
		when(encrypter.apply(anyString())).thenAnswer(a -> "ENC:" + a.getArgument(0));
	}

	@Test
	void testRegisterWithExistingUsername() {
		UserRegistrationData registrationData = mock(UserRegistrationData.class);
		when(registrationData.getUsername()).thenReturn(USERNAME1);
		when(userDao.usernameExists(USERNAME1)).thenReturn(true);

		assertDuplicateUsername(() -> sut.register(registrationData));
	}

	@Test
	void testRegisterWithExistingEmail() {
		UserRegistrationData registrationData = mock(UserRegistrationData.class);
		when(registrationData.getEmail()).thenReturn(EMAIL1);
		when(userDao.emailExists(EMAIL1)).thenReturn(true);

		assertDuplicateEmail(() -> sut.register(registrationData));
	}

	@Test
	void testRegister() {
		UserRegistrationData registrationData = mock(UserRegistrationData.class);
		when(registrationData.getUsername()).thenReturn(USERNAME1);
		when(registrationData.getEmail()).thenReturn(EMAIL1);
		when(registrationData.getPassword()).thenReturn(PASSWORD1);
		when(registrationData.getImageUrl()).thenReturn(IMAGE_URL1);
		when(registrationData.getBio()).thenReturn(BIO1);
		when(userDao.create(any(UserData.class), anyString())).then(x -> ImmutableUserData.builder().from(x.getArgument(0)).id(USERID1).build());
		when(encrypter.apply(PASSWORD1)).thenReturn(ENCRYPTED_PASSWORD);

		UserData result = sut.register(registrationData);

		verify(userDao).create(any(UserData.class), eq(ENCRYPTED_PASSWORD));
		assertEquals(USERNAME1, result.getUsername());
		assertEquals(EMAIL1, result.getEmail());
		assertEquals(IMAGE_URL1, result.getImageUrl());
		verify(biographyService).create(USERID1, BIO1);
	}

	@Test
	void testFindByNameForNonExistingUser() {
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.empty());
		try {
			sut.findByUserName(USERNAME1);
			fail("should throw for non-existing user");
		}
		catch( EntityDoesNotExistException e ) {
			// expected
		}
	}

	@Test
	void testFindByName() {
		UserData userData = mock(UserData.class);
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(userData));
		UserData result = sut.findByUserName(USERNAME1);
		assertSame(userData, result);
	}

	@Test
	void testFindByEmailAndPasswordForNonExistingUser() {
		when(userDao.findByEmailAndPassword(EMAIL1, "ENC:" + PASSWORD1)).thenReturn(Optional.empty());
		try {
			sut.findByEmailAndPassword(EMAIL1, PASSWORD1);
			fail("should throw for non-existing user");
		}
		catch( EntityDoesNotExistException e ) {
			// expected
		}
	}

	@Test
	void testFindByEmailAndPassword() {
		UserData userData = mock(UserData.class);
		when(userDao.findByEmailAndPassword(EMAIL1, "ENC:" + PASSWORD1)).thenReturn(Optional.of(userData));
		UserData result = sut.findByEmailAndPassword(EMAIL1, PASSWORD1);
		assertSame(userData, result);
	}

	@Test
	void testUpdateWithExistingUsername() {
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(true);
		when(userUpdateData.getUsername()).thenReturn(USERNAME2);
		when(userDao.usernameExists(USERNAME2)).thenReturn(true);

		assertDuplicateUsername(() -> sut.update(userUpdateData));
	}

	@Test
	void testUpdateWithSameUsername() {
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));
		UserUpdateOperation updateOp = mockUserUpdateOperation();
		when(userDao.createUpdate()).thenReturn(updateOp);

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(true);
		when(userUpdateData.getUsername()).thenReturn(USERNAME1);

		sut.update(userUpdateData);

		verify(updateOp).executeForId(USERID1);
		verify(biographyService, never()).updateById(any(), any());
	}

	@Test
	void testUpdateWithExistingEmail() {
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(false);
		when(userUpdateData.isExplicitlySet(PropName.EMAIL)).thenReturn(true);
		when(userUpdateData.getEmail()).thenReturn(EMAIL2);
		when(userDao.emailExists(EMAIL2)).thenReturn(true);

		assertDuplicateEmail(() -> sut.update(userUpdateData));
	}

	@Test
	void testUpdateWithSameEmail() {
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));
		UserUpdateOperation updateOp = mockUserUpdateOperation();
		when(userDao.createUpdate()).thenReturn(updateOp);

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(false);
		when(userUpdateData.isExplicitlySet(PropName.EMAIL)).thenReturn(true);
		when(userUpdateData.getEmail()).thenReturn(EMAIL1);

		sut.update(userUpdateData);

		verify(updateOp).executeForId(USERID1);
		verify(biographyService, never()).updateById(any(), any());
	}

	@Test
	void testUpdate() {
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));
		UserUpdateOperation updateOp = mockUserUpdateOperation();
		when(userDao.createUpdate()).thenReturn(updateOp);

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(any(PropName.class))).thenReturn(true);
		when(userUpdateData.getUsername()).thenReturn(USERNAME2);
		when(userUpdateData.getEmail()).thenReturn(EMAIL2);
		when(userUpdateData.getBio()).thenReturn(BIO2);
		when(userUpdateData.getImageUrl()).thenReturn(IMAGE_URL2);
		when(userUpdateData.getPassword()).thenReturn(PASSWORD2);

		sut.update(userUpdateData);

		verify(updateOp).setUsername(true, USERNAME2);
		verify(updateOp).setEmail(true, EMAIL2);
		verify(updateOp).setImageUrl(true, IMAGE_URL2);
		verify(updateOp).setPassword(true, "ENC:" + PASSWORD2);
		verify(updateOp).executeForId(USERID1);
		verify(biographyService).updateById(USERID1, BIO2);
	}

	private void assertDuplicateUsername(Runnable f) {
		try {
			f.run();
			fail("duplicate username should have been reported!");
		}
		catch( SimpleValidationException e ) {
			assertNotNull(e.getViolations());
			assertEquals(1, e.getViolations().size());
			assertEquals("username", e.getViolations().get(0).getFieldName());
			assertEquals("duplicate user name", e.getViolations().get(0).getMessage());
		}
	}

	private void assertDuplicateEmail(Runnable f) {
		try {
			f.run();
			fail("duplicate email should have been reported!");
		}
		catch( SimpleValidationException e ) {
			assertNotNull(e.getViolations());
			assertEquals(1, e.getViolations().size());
			assertEquals("email", e.getViolations().get(0).getFieldName());
			assertEquals("duplicate email", e.getViolations().get(0).getMessage());
		}
	}

	private UserData mockCurrentUser() {
		User user = mock(User.class, Mockito.withSettings().lenient());
		when(user.getName()).thenReturn(USERNAME1);
		when(user.getUniqueId()).thenReturn(USERID1);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);

		return ImmutableUserData.builder().id(USERID1).username(USERNAME1).email(EMAIL1).imageUrl("").build();
	}

	private UserUpdateOperation mockUserUpdateOperation() {
		return mock(UserUpdateOperation.class, invocation -> {
			if( UserUpdateOperation.class.isAssignableFrom(invocation.getMethod().getReturnType()) ) {
				return invocation.getMock();
			}
			return null;
		});
	}
}