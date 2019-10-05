package realworld.authorization.service;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.NotAuthorizedException;

/**
 * Tests for the {@link AuthorizationImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class AuthorizationImplTest {

	private static final String USERNAME = "username";
	private static final String USER_ID = "userid";

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private AuthorizationImpl sut;

	@Test
	void testRequireLoginWithoutUser() {
		when(authenticationContext.getUserPrincipal()).thenReturn(null);
		try {
			sut.requireLogin();
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireLogin() {
		when(authenticationContext.getUserPrincipal()).thenReturn(mock(User.class));
		sut.requireLogin();
	}

	@Test
	void testRequireUsernameWithoutUser() {
		when(authenticationContext.getUserPrincipal()).thenReturn(null);
		try {
			sut.requireUsername(USERNAME);
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireUsernameWithOtherUser() {
		User user = mock(User.class);
		when(user.getName()).thenReturn("otheruser");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		try {
			sut.requireUsername(USERNAME);
			fail("should have thrown");
		}
		catch( NotAuthorizedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireUsername() {
		User user = mock(User.class);
		when(user.getName()).thenReturn(USERNAME);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		sut.requireUsername(USERNAME);
	}

	@Test
	void testRequireUserIdWithoutUser() {
		when(authenticationContext.getUserPrincipal()).thenReturn(null);
		try {
			sut.requireUserId(USER_ID);
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireUserIdWithOtherUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("otherid");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		try {
			sut.requireUserId(USER_ID);
			fail("should have thrown");
		}
		catch( NotAuthorizedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireUserIdWithNullInput() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn("otherid");
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		try {
			sut.requireUserId(null);
			fail("should have thrown");
		}
		catch( NotAuthorizedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireUserId() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		sut.requireUserId(USER_ID);
	}

	@Test
	void testRequireSystemWithoutUser() {
		when(authenticationContext.isSystem()).thenReturn(false);
		try {
			sut.requireSystemUser();
			fail("should have thrown");
		}
		catch( NotAuthorizedException expected ) {
			// expected
		}
	}

	@Test
	void testRequireSystem() {
		when(authenticationContext.isSystem()).thenReturn(true);
		sut.requireSystemUser();
	}
}
