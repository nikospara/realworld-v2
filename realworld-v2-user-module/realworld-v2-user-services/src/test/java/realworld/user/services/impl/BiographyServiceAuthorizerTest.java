package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.service.Authorization;
import realworld.user.services.BiographyService;

/**
 * Tests for the {@link BiographyServiceAuthorizer}.
 */
@EnableAutoWeld
@AddBeanClasses(BiographyServiceAuthorizerTest.DummyBiographyService.class)
@AddEnabledDecorators(BiographyServiceAuthorizer.class)
@ExtendWith(MockitoExtension.class)
public class BiographyServiceAuthorizerTest {

	private static final String USERNAME_TO_FIND = "USERNAME_TO_FIND";
	private static final String USERNAME_TO_UPDATE = "USERNAME_TO_UPDATE";
	private static final String USER_ID_TO_UPDATE = "USER_ID_TO_UPDATE";
	private static final String CONTENT = "Content";
	private static final String FROM_FIND_BY_USER_NAME = "FROM_FIND_BY_USER_NAME";

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private DummyBiographyService dummy;

	@Test
	void testFindByUserName() {
		Object result = dummy.findByUserName(USERNAME_TO_FIND);
		assertSame(FROM_FIND_BY_USER_NAME, result);
	}

	@Test
	void testUpdateByUserNameWithWrongUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireUsername(USERNAME_TO_UPDATE);
		try {
			dummy.updateByUserName(USERNAME_TO_UPDATE, "XXX");
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}

	@Test
	void testUpdateByUserName() {
		dummy.updateByUserName(USERNAME_TO_UPDATE, CONTENT);
		verify(authorization).requireUsername(USERNAME_TO_UPDATE);
	}

	@Test
	void testUpdateByIdWithWrongUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireUserId(USER_ID_TO_UPDATE);
		try {
			dummy.updateById(USER_ID_TO_UPDATE, "XXX");
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}

	@Test
	void testUpdateById() {
		dummy.updateById(USER_ID_TO_UPDATE, CONTENT);
		verify(authorization).requireUserId(USER_ID_TO_UPDATE);
	}

	@ApplicationScoped
	static class DummyBiographyService implements BiographyService {
		@Override
		public String findByUserName(String username) {
			if( username != USERNAME_TO_FIND ) {
				throw new IllegalArgumentException();
			}
			return FROM_FIND_BY_USER_NAME;
		}

		@Override
		public void updateByUserName(String username, String content) {
			if( username != USERNAME_TO_UPDATE ) {
				throw new IllegalArgumentException();
			}
		}

		@Override
		public void updateById(String userId, String content) {
			if( userId != USER_ID_TO_UPDATE ) {
				throw new IllegalArgumentException();
			}
		}
	}
}
