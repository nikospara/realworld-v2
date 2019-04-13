package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static realworld.authorization.service.Authorization.REDUCTED;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.validation.Valid;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.EntityDoesNotExistException;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.authorization.service.Authorization;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.services.UserService;

/**
 * Tests for the {@link UserServiceAuthorizer}.
 */
@EnableAutoWeld
@AddBeanClasses(UserServiceAuthorizerTest.DummyUserService.class)
@AddEnabledDecorators(UserServiceAuthorizer.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceAuthorizerTest {

	private static final UserRegistrationData USER_REG_DATA = mock(UserRegistrationData.class);
	private static final String USERNAME_TO_FIND = "USERNAME_TO_FIND";
	private static final String EMAIL_FOR_FIND_BY_EMAIL_PASSWORD = "EMAIL_FOR_FIND_BY_EMAIL_PASSWORD";
	private static final String PASSWD_FOR_FIND_BY_EMAIL_PASSWORD = "PASSWD_FOR_FIND_BY_EMAIL_PASSWORD";

	private static final UserData FROM_REGISTER = mock(UserData.class, "FROM_REGISTER");
	private static final UserData FROM_FIND_BY_USER_NAME = ImmutableUserData.builder().id("FROM_FIND_BY_USER_NAME").username(USERNAME_TO_FIND).email("EMAIL_FROM_FIND_BY_USER_NAME").imageUrl("IMAGE_FROM_FIND_BY_USER_NAME").build();
	private static final UserData FROM_FIND_BY_EMAIL_PASSWORD = ImmutableUserData.builder().id("FROM_FIND_BY_EMAIL_PASSWORD").username(USERNAME_TO_FIND).email(EMAIL_FOR_FIND_BY_EMAIL_PASSWORD).imageUrl("IMAGE_FROM_FIND_BY_EMAIL_PASSWORD").build();

	@Produces @Mock
	private Authorization authorization;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private DummyUserService dummy;

	@Test
	void testRegister() {
		Object result = dummy.register(USER_REG_DATA);
		assertSame(FROM_REGISTER, result);
	}

	@Test
	void testFindByUserNameForCurrentUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("FROM_FIND_BY_USER_NAME");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		UserData result = dummy.findByUserName(USERNAME_TO_FIND);
		assertEquals(FROM_FIND_BY_USER_NAME, result);
	}

	@Test
	void testFindByUserNameForOtherUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("ANOTHER_ID");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		UserData result = dummy.findByUserName(USERNAME_TO_FIND);
		assertEquals(REDUCTED, result.getId());
		assertEquals(USERNAME_TO_FIND, result.getUsername());
		assertEquals(REDUCTED, result.getEmail());
		assertEquals("IMAGE_FROM_FIND_BY_USER_NAME", result.getImageUrl());
	}

	@Test
	void testFindByUserNameForNoUser() {
		when(authenticationContext.getUserPrincipal()).thenReturn(null);
		UserData result = dummy.findByUserName(USERNAME_TO_FIND);
		assertEquals(REDUCTED, result.getId());
		assertEquals(USERNAME_TO_FIND, result.getUsername());
		assertEquals(REDUCTED, result.getEmail());
		assertEquals("IMAGE_FROM_FIND_BY_USER_NAME", result.getImageUrl());
	}

	@Test
	void testFindByEmailAndPassword() {
		Object result = dummy.findByEmailAndPassword(EMAIL_FOR_FIND_BY_EMAIL_PASSWORD, PASSWD_FOR_FIND_BY_EMAIL_PASSWORD);
		assertSame(FROM_FIND_BY_EMAIL_PASSWORD, result);
	}

	@ApplicationScoped
	static class DummyUserService implements UserService {
		@Override
		public UserData register(@Valid UserRegistrationData registrationData) {
			if( registrationData != USER_REG_DATA ) {
				throw new IllegalArgumentException();
			}
			return FROM_REGISTER;
		}

		@Override
		public UserData findByUserName(String username) throws EntityDoesNotExistException {
			if( username != USERNAME_TO_FIND ) {
				throw new IllegalArgumentException();
			}
			return FROM_FIND_BY_USER_NAME;
		}

		@Override
		public UserData findByEmailAndPassword(String email, String password) {
			if( email != EMAIL_FOR_FIND_BY_EMAIL_PASSWORD || password != PASSWD_FOR_FIND_BY_EMAIL_PASSWORD ) {
				throw new IllegalArgumentException();
			}
			return FROM_FIND_BY_EMAIL_PASSWORD;
		}
	}
}
