package realworld.user.services.authz.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_ID;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_NAME;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;
import static realworld.authorization.service.Authorization.REDUCTED;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authentication.User;
import realworld.authentication.UserImpl;
import realworld.authorization.NotAuthenticatedException;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;
import realworld.user.services.UserService;

/**
 * Tests for the {@link UserServiceAuthorizer}.
 */
@EnableAutoWeld
@AddEnabledDecorators(UserServiceAuthorizer.class)
@AddBeanClasses({UserServiceAuthorizerTest.DummyUserService.class, AuthorizerDependenciesProducer.class})
@ExtendWith(MockitoExtension.class)
public class UserServiceAuthorizerTest {

	public interface SpyUserService extends UserService {
		String getLastCall();
	}

	public static class DummyUserService implements SpyUserService {
		private String lastCall;

		@Override
		public String getLastCall() {
			return lastCall;
		}

		@Override
		public UserData register(UserUpdateData registrationData) {
			return FROM_REGISTER;
		}

		@Override
		public UserData findByUserName(String username) {
			return FROM_FIND_BY_USER_NAME;
		}

		@Override
		public void update(UserUpdateData userUpdateData) {
			lastCall = LAST_CALL_UPDATE;
		}
	}

	private static final UserUpdateData USER_REG_DATA = new UserUpdateData();
	private static final String USERNAME_TO_FIND = "USERNAME_TO_FIND";
	private static final String USER_ID_OTHER = "USER_ID_OTHER";
	private static final String USER_ID_TO_UPDATE = "USER_ID_TO_UPDATE";
	private static final UserUpdateData USER_UPDATE_DATA = mock(UserUpdateData.class);

	private static final UserData FROM_REGISTER = mock(UserData.class, "FROM_REGISTER");
	private static final UserData FROM_FIND_BY_USER_NAME = ImmutableUserData.builder().id("FROM_FIND_BY_USER_NAME").username(USERNAME_TO_FIND).email("EMAIL_FROM_FIND_BY_USER_NAME").imageUrl("IMAGE_FROM_FIND_BY_USER_NAME").build();
	private static final String LAST_CALL_UPDATE = "LAST_CALL_UPDATE";

	@Inject
	private AuthorizerDependenciesProducer dependenciesProducer;

	@Inject
	private SpyUserService sut;

	@Test
	void testRegister() {
		Object result = sut.register(USER_REG_DATA);
		assertSame(FROM_REGISTER, result);
	}

	@Test
	void testFindByUserNameForCurrentUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("FROM_FIND_BY_USER_NAME");
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		UserData result = sut.findByUserName(USERNAME_TO_FIND);
		assertSame(FROM_FIND_BY_USER_NAME, result);
	}

	@Test
	void testFindByUserNameForOtherUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("ANOTHER_ID");
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		UserData result = sut.findByUserName(USERNAME_TO_FIND);
		assertEquals(REDUCTED, result.getId());
		assertEquals(USERNAME_TO_FIND, result.getUsername());
		assertEquals(REDUCTED, result.getEmail());
		assertEquals("IMAGE_FROM_FIND_BY_USER_NAME", result.getImageUrl());
	}

	@Test
	void testFindByUserNameForNoUser() {
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(null);
		UserData result = sut.findByUserName(USERNAME_TO_FIND);
		assertEquals(REDUCTED, result.getId());
		assertEquals(USERNAME_TO_FIND, result.getUsername());
		assertEquals(REDUCTED, result.getEmail());
		assertEquals("IMAGE_FROM_FIND_BY_USER_NAME", result.getImageUrl());
	}

	@Test
	void testUpdateWithoutLogin() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireLogin();
		expectNotAuthenticatedException(() -> sut.update(USER_UPDATE_DATA));
		assertNull(sut.getLastCall());
	}

	@Test
	void testUpdateWithOtherLogin() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_OTHER);
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		expectNotAuthenticatedException(() -> sut.update(USER_UPDATE_DATA));
		assertNull(sut.getLastCall());
	}

	@Test
	void testUpdateWithSystemUser() {
		User user = new UserImpl(SYSTEM_USER_NAME, SYSTEM_USER_ID);
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		sut.update(USER_UPDATE_DATA);
		assertEquals(LAST_CALL_UPDATE, sut.getLastCall());
	}

	@Test
	void testUpdate() {
		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.getId()).thenReturn(USER_ID_TO_UPDATE);
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_TO_UPDATE);
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		sut.update(userUpdateData);
		assertEquals(LAST_CALL_UPDATE, sut.getLastCall());
	}
}
