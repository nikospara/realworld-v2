package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.service.Authorization;

/**
 * Tests for the {@link UserServiceAuthorizerImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceAuthorizerImplTest {

	private static final String USERNAME = "USERNAME";
	private static final String USER_ID = "USER_ID";

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private UserServiceAuthorizerImpl sut;

	@Test
	void testAddForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		BiConsumer<String, String> mockDelegate = mock(BiConsumer.class);
		expectNotAuthenticatedException(() -> sut.add(USER_ID, USERNAME, mockDelegate));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	void testAddForSystemUser() {
		doNothing().when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		BiConsumer<String, String> mockDelegate = mock(BiConsumer.class);
		sut.add(USER_ID, USERNAME, mockDelegate);
		verify(mockDelegate).accept(eq(USER_ID), eq(USERNAME));
	}

	@Test
	void testUpdateUsernameForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		BiConsumer<String, String> mockDelegate = mock(BiConsumer.class);
		expectNotAuthenticatedException(() -> sut.updateUsername(USER_ID, USERNAME, mockDelegate));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	void testUpdateUsernameForSystemUser() {
		doNothing().when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		BiConsumer<String, String> mockDelegate = mock(BiConsumer.class);
		sut.updateUsername(USER_ID, USERNAME, mockDelegate);
		verify(mockDelegate).accept(eq(USER_ID), eq(USERNAME));
	}

	@Test
	void testFindByUserNameForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		Function<String, Optional<String>> mockDelegate = mock(Function.class);
		expectNotAuthenticatedException(() -> sut.findByUserName(USERNAME, mockDelegate));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	void testFindByUserNameForSystemUser() {
		doNothing().when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		Function<String, Optional<String>> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(eq(USERNAME))).thenReturn(Optional.of(USER_ID));
		Optional<String> result = sut.findByUserName(USERNAME, mockDelegate);
		verify(mockDelegate).apply(eq(USERNAME));
		assertEquals(USER_ID, result.get());
	}

	@Test
	void testFindByIdForNonSystemUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		Function<String, Optional<String>> mockDelegate = mock(Function.class);
		expectNotAuthenticatedException(() -> sut.findByUserId(USER_ID, mockDelegate));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	void testFindByIdForSystemUser() {
		doNothing().when(authorization).requireSystemUser();
		@SuppressWarnings("unchecked")
		Function<String, Optional<String>> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(eq(USER_ID))).thenReturn(Optional.of(USERNAME));
		Optional<String> result = sut.findByUserId(USER_ID, mockDelegate);
		verify(mockDelegate).apply(eq(USER_ID));
		assertEquals(USERNAME, result.get());
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
