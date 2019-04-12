package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.SimpleValidationException;
import realworld.user.dao.UserDao;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;

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
	private static final String BIO2 = "BIO2";
	private static final String IMAGE2 = "IMAGE2";
	private static final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";

	@Produces @Mock
	private UserDao userDao;

	@Produces @Mock(lenient = true)
	private PasswordEncrypter encrypter;

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
		when(userDao.create(any(UserData.class), anyString())).then(x -> x.getArgument(0));
		when(encrypter.apply(PASSWORD1)).thenReturn(ENCRYPTED_PASSWORD);

		UserData result = sut.register(registrationData);

		verify(userDao).create(any(UserData.class), eq(ENCRYPTED_PASSWORD));
		assertEquals(USERNAME1, result.getUsername());
		assertEquals(EMAIL1, result.getEmail());
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
}
