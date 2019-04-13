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
//	private static final UserData FROM_GET_CURENT_USER = mock(UserData.class, "FROM_GET_CURENT_USER");
//	private static final UserData FROM_UPDATE = mock(UserData.class, "FROM_UPDATE");
	private static final UserData FROM_REGISTER = mock(UserData.class, "FROM_REGISTER");
	private static final UserData FROM_FIND_BY_USER_NAME = ImmutableUserData.builder().id("FROM_FIND_BY_USER_NAME").username(USERNAME_TO_FIND).email("EMAIL_FROM_FIND_BY_USER_NAME").imageUrl("IMAGE_FROM_FIND_BY_USER_NAME").build();
//	private static final UserData FROM_LOGIN = mock(UserData.class, "FROM_LOGIN");
//	private static final ProfileData FROM_FOLLOW = mock(ProfileData.class, "FROM_FOLLOW");
//	private static final ProfileData FROM_UNFOLLOW = mock(ProfileData.class, "FROM_UNFOLLOW");
//	private static final ProfileData FROM_FIND_PROFILE = mock(ProfileData.class, "FROM_FIND_PROFILE");
//	private static final UserUpdateData USER_UPDATE_DATA = mock(UserUpdateData.class);
//	private static final UserLoginData USER_LOGIN_DATA = mock(UserLoginData.class);
//	private static final String USER_TO_FOLLOW = "USER_TO_FOLLOW";
//	private static final String USER_TO_UNFOLLOW = "USER_TO_UNFOLLOW";
//	private static final String USER_TO_FIND_PROFILE = "USER_TO_FIND_PROFILE";
//	private static final String USER_TO_FIND_FOLLOWED = "USER_TO_FIND_FOLLOWED";
//	private static final List<String> FROM_FIND_FOLLOWED_USER_IDS = new ArrayList<>();

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
	}
}
