package realworld.article.services.authz.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static realworld.authorization.AuthorizationAssertions.expectNotAuthenticatedException;

import javax.inject.Inject;
import java.util.Optional;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.article.services.UserService;
import realworld.authorization.NotAuthenticatedException;

/**
 * Tests for the {@link UserServiceAuthorizer}.
 */
@EnableAutoWeld
@AddEnabledDecorators(UserServiceAuthorizer.class)
@AddBeanClasses({UserServiceAuthorizerTest.DummyUserService.class, UserServiceAuthorizerDependenciesProducer.class})
@ExtendWith(MockitoExtension.class)
public class UserServiceAuthorizerTest {

	public static class DummyUserService implements UserService {
		@Override
		public void add(String id, String username) {

		}

		@Override
		public void updateUsername(String id, String username) {

		}

		@Override
		public Optional<String> findByUserName(String username) {
			return Optional.of(USER_ID);
		}

		@Override
		public Optional<String> findByUserId(String id) {
			return Optional.of(USERNAME);
		}
	}

	private static final String USERNAME = "USERNAME";
	private static final String USER_ID = "USER_ID";

	@Inject
	private UserServiceAuthorizerDependenciesProducer dependenciesProducer;

	@Inject
	private UserService sut;

	@Test
	void testAddForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		expectNotAuthenticatedException(() -> sut.add(USER_ID, USERNAME));
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
	}

	@Test
	void testAddForSystemUser() {
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		sut.add(USER_ID, USERNAME);
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
	}

	@Test
	void testUpdateUsernameForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		expectNotAuthenticatedException(() -> sut.updateUsername(USER_ID, USERNAME));
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
	}

	@Test
	void testUpdateUsernameForSystemUser() {
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		sut.updateUsername(USER_ID, USERNAME);
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
	}

	@Test
	void testFindByUserNameForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		expectNotAuthenticatedException(() -> sut.findByUserName(USERNAME));
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
	}

	@Test
	void testFindByUserNameForSystemUser() {
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		Optional<String> result = sut.findByUserName(USERNAME);
		assertEquals(USER_ID, result.get());
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
	}

	@Test
	void testFindByIdForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(dependenciesProducer.getAuthorization()).requireSystemUser();
		expectNotAuthenticatedException(() -> sut.findByUserId(USER_ID));
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
		verifyNoMoreInteractions(dependenciesProducer.getAuthorization());
	}

	@Test
	void testFindByIdForSystemUser() {
		doNothing().when(dependenciesProducer.getAuthorization()).requireSystemUser();
		Optional<String> result = sut.findByUserId(USER_ID);
		assertEquals(USERNAME, result.get());
		verify(dependenciesProducer.getAuthorization()).requireSystemUser();
	}
}
