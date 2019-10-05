package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_ID;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_NAME;
import static realworld.authorization.service.Authorization.REDUCTED;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.authentication.UserImpl;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.service.Authorization;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;

/**
 * Tests for the {@link UserServiceAuthorizerImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceAuthorizerImplTest {

	private static final UserUpdateData USER_REG_DATA = new UserUpdateData();
	private static final String USERNAME_TO_FIND = "USERNAME_TO_FIND";
	private static final String USER_ID_OTHER = "USER_ID_OTHER";
	private static final String USER_ID_TO_UPDATE = "USER_ID_TO_UPDATE";
	private static final UserUpdateData USER_UPDATE_DATA = mock(UserUpdateData.class);

	private static final UserData FROM_REGISTER = mock(UserData.class, "FROM_REGISTER");
	private static final UserData FROM_FIND_BY_USER_NAME = ImmutableUserData.builder().id("FROM_FIND_BY_USER_NAME").username(USERNAME_TO_FIND).email("EMAIL_FROM_FIND_BY_USER_NAME").imageUrl("IMAGE_FROM_FIND_BY_USER_NAME").build();

	@Produces @Mock
	private Authorization authorization;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private UserServiceAuthorizerImpl sut;

	@Test
	void testRegister() {
		@SuppressWarnings("unchecked")
		Function<UserUpdateData,UserData> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(any(UserUpdateData.class))).thenReturn(FROM_REGISTER);
		Object result = sut.register(USER_REG_DATA, mockDelegate);
		assertSame(FROM_REGISTER, result);
		verify(mockDelegate).apply(USER_REG_DATA);
	}

	@Test
	void testFindByUserNameForCurrentUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("FROM_FIND_BY_USER_NAME");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		@SuppressWarnings("unchecked")
		Function<String,UserData> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(USERNAME_TO_FIND)).thenReturn(FROM_FIND_BY_USER_NAME);
		UserData result = sut.findByUserName(USERNAME_TO_FIND, mockDelegate);
		assertSame(FROM_FIND_BY_USER_NAME, result);
	}

	@Test
	void testFindByUserNameForOtherUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("ANOTHER_ID");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		@SuppressWarnings("unchecked")
		Function<String,UserData> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(USERNAME_TO_FIND)).thenReturn(FROM_FIND_BY_USER_NAME);
		UserData result = sut.findByUserName(USERNAME_TO_FIND, mockDelegate);
		assertEquals(REDUCTED, result.getId());
		assertEquals(USERNAME_TO_FIND, result.getUsername());
		assertEquals(REDUCTED, result.getEmail());
		assertEquals("IMAGE_FROM_FIND_BY_USER_NAME", result.getImageUrl());
	}

	@Test
	void testFindByUserNameForNoUser() {
		when(authenticationContext.getUserPrincipal()).thenReturn(null);
		@SuppressWarnings("unchecked")
		Function<String,UserData> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(USERNAME_TO_FIND)).thenReturn(FROM_FIND_BY_USER_NAME);
		UserData result = sut.findByUserName(USERNAME_TO_FIND, mockDelegate);
		assertEquals(REDUCTED, result.getId());
		assertEquals(USERNAME_TO_FIND, result.getUsername());
		assertEquals(REDUCTED, result.getEmail());
		assertEquals("IMAGE_FROM_FIND_BY_USER_NAME", result.getImageUrl());
	}

	@Test
	void testUpdateWithoutLogin() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireLogin();
		@SuppressWarnings("unchecked")
		Consumer<UserUpdateData> mockDelegate = mock(Consumer.class);
		expectNotAuthenticatedException(() -> sut.update(USER_UPDATE_DATA, mockDelegate));
		verifyZeroInteractions(mockDelegate);
	}

	@Test
	void testUpdateWithOtherLogin() {
		@SuppressWarnings("unchecked")
		Consumer<UserUpdateData> mockDelegate = mock(Consumer.class);
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_OTHER);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		doThrow(NotAuthenticatedException.class).when(authorization).requireSystemUser();
		expectNotAuthenticatedException(() -> sut.update(USER_UPDATE_DATA, mockDelegate));
		verifyZeroInteractions(mockDelegate);
	}

	@Test
	void testUpdateWithSystemUser() {
		@SuppressWarnings("unchecked")
		Consumer<UserUpdateData> mockDelegate = mock(Consumer.class);
		User user = new UserImpl(SYSTEM_USER_NAME, SYSTEM_USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		doNothing().when(authorization).requireSystemUser();
		sut.update(USER_UPDATE_DATA, mockDelegate);
		verify(mockDelegate).accept(USER_UPDATE_DATA);
	}

	@Test
	void testUpdate() {
		@SuppressWarnings("unchecked")
		Consumer<UserUpdateData> mockDelegate = mock(Consumer.class);
		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.getId()).thenReturn(USER_ID_TO_UPDATE);
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_TO_UPDATE);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		sut.update(userUpdateData, mockDelegate);
		verify(mockDelegate).accept(userUpdateData);
	}

	private void expectNotAuthenticatedException(Runnable f) {
		try {
			f.run();
			fail("expected NotAuthenticatedException");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}
}
