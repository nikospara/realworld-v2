package realworld.user.services.authz.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_ID;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_NAME;

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
import realworld.user.services.BiographyService;

/**
 * Tests for the {@link BiographyServiceAuthorizer}.
 */
@EnableAutoWeld
@AddEnabledDecorators(BiographyServiceAuthorizer.class)
@AddBeanClasses({BiographyServiceAuthorizerTest.DummyBiographyService.class, AuthorizerDependenciesProducer.class})
@ExtendWith(MockitoExtension.class)
public class BiographyServiceAuthorizerTest {

	public interface SpyBiographyService extends BiographyService {
		String getLastCall();
	}

	public static class DummyBiographyService implements SpyBiographyService {
		private String lastCall;

		@Override
		public String getLastCall() {
			return lastCall;
		}

		@Override
		public void create(String userId, String content) {
			lastCall = LAST_CALL_CREATE;
		}

		@Override
		public String findByUserName(String username) {
			return FROM_FIND_BY_USER_NAME;
		}

		@Override
		public void updateByUserName(String username, String content) {
			lastCall = LAST_CALL_UPDATE_BY_USER_NAME;
		}

		@Override
		public void updateById(String userId, String content) {
			lastCall = LAST_CALL_UPDATE_BY_ID;
		}
	}

	private static final String USER_ID_TO_CREATE = "USER_ID_TO_CREATE";
	private static final String USERNAME_TO_FIND = "USERNAME_TO_FIND";
	private static final String USERNAME_TO_UPDATE = "USERNAME_TO_UPDATE";
	private static final String USER_ID_TO_UPDATE = "USER_ID_TO_UPDATE";
	private static final String USER_ID_OTHER = "USER_ID_OTHER";
	private static final String CONTENT = "Content";
	private static final String FROM_FIND_BY_USER_NAME = "FROM_FIND_BY_USER_NAME";
	private static final String LAST_CALL_CREATE = "LAST_CALL_CREATE";
	private static final String LAST_CALL_UPDATE_BY_USER_NAME = "LAST_CALL_UPDATE_BY_USER_NAME";
	private static final String LAST_CALL_UPDATE_BY_ID = "LAST_CALL_UPDATE_BY_ID";

	@Inject
	private AuthorizerDependenciesProducer dependenciesProducer;

	@Inject
	private SpyBiographyService sut;

	@Test
	void testCreate() {
		sut.create(USER_ID_TO_CREATE, CONTENT);
		assertEquals(LAST_CALL_CREATE, sut.getLastCall());
	}

	@Test
	void testFindByUserName() {
		Object result = sut.findByUserName(USERNAME_TO_FIND);
		assertSame(FROM_FIND_BY_USER_NAME, result);
	}

	@Test
	void testUpdateByUserNameWithWrongUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_TO_UPDATE);
		try {
			sut.updateByUserName(USERNAME_TO_UPDATE, "XXX");
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
		assertNull(sut.getLastCall());
	}

	@Test
	void testUpdateByUserName() {
		sut.updateByUserName(USERNAME_TO_UPDATE, CONTENT);
		verify(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_TO_UPDATE);
		assertEquals(LAST_CALL_UPDATE_BY_USER_NAME, sut.getLastCall());
	}

	@Test
	void testUpdateByIdWithoutUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireLogin();
		try {
			sut.updateById(USER_ID_TO_UPDATE, "XXX");
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
		assertNull(sut.getLastCall());
	}

	@Test
	void testUpdateByIdWithOtherUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_OTHER);
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		try {
			sut.updateById(USER_ID_TO_UPDATE, "XXX");
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
		assertNull(sut.getLastCall());
	}

	@Test
	void testUpdateByIdWithSystemUser() {
		User user = new UserImpl(SYSTEM_USER_NAME, SYSTEM_USER_ID);
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		sut.updateById(USER_ID_TO_UPDATE, CONTENT);
		assertEquals(LAST_CALL_UPDATE_BY_ID, sut.getLastCall());
	}

	@Test
	void testUpdateByIdSameUser() {
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_TO_UPDATE);
		when(dependenciesProducer.getAuthenticationContext().getUserPrincipal()).thenReturn(user);
		sut.updateById(USER_ID_TO_UPDATE, CONTENT);
		assertEquals(LAST_CALL_UPDATE_BY_ID, sut.getLastCall());
	}
}
