package realworld.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link AuthenticationContextImpl}.
 */
public class AuthenticationContextImplTest {

	private static final String USER_NAME = "user_name";
	private static final String USER_ID = UUID.randomUUID().toString();

	@Test
	void testUnauthenticatedHasNoPrincipal() {
		assertNull(AuthenticationContextImpl.unauthenticated().getUserPrincipal());
	}

	@Test
	void testUnauthenticatedIsNotSystem() {
		assertFalse(AuthenticationContextImpl.unauthenticated().isSystem());
	}

	@Test
	void testSystemHasPrincipal() {
		AuthenticationContext sut = AuthenticationContextImpl.system();
		assertTrue(sut.isSystem());
		assertEquals(AuthenticationContextImpl.SYSTEM_USER_ID, sut.getUserPrincipal().getUniqueId());
		assertEquals(AuthenticationContextImpl.SYSTEM_USER_NAME, sut.getUserPrincipal().getName());
	}

	@Test
	void testForUser() {
		UserImpl user = new UserImpl(USER_ID, USER_NAME);
		AuthenticationContext sut = AuthenticationContextImpl.forUser(user);
		assertNotNull(sut.getUserPrincipal());
		assertFalse(sut.isSystem());
		assertSame(user, sut.getUserPrincipal());
	}
}
