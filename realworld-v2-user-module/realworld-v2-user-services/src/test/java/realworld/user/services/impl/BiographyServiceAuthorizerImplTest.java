package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_ID;
import static realworld.authentication.AuthenticationContextImpl.SYSTEM_USER_NAME;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.function.BiConsumer;
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

/**
 * Tests for the {@link BiographyServiceAuthorizerImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class BiographyServiceAuthorizerImplTest {

	private static final String USER_ID_TO_CREATE = "USER_ID_TO_CREATE";
	private static final String USERNAME_TO_FIND = "USERNAME_TO_FIND";
	private static final String USERNAME_TO_UPDATE = "USERNAME_TO_UPDATE";
	private static final String USER_ID_TO_UPDATE = "USER_ID_TO_UPDATE";
	private static final String USER_ID_OTHER = "USER_ID_OTHER";
	private static final String CONTENT = "Content";
	private static final String FROM_FIND_BY_USER_NAME = "FROM_FIND_BY_USER_NAME";

	@Produces @Mock
	private Authorization authorization;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private BiographyServiceAuthorizerImpl sut;

	@Test
	void testCreate() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		sut.create(USER_ID_TO_CREATE, CONTENT, mockDelegate);
		verify(mockDelegate).accept(USER_ID_TO_CREATE, CONTENT);
	}

	@Test
	void testFindByUserName() {
		@SuppressWarnings("unchecked")
		Function<String,String> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(anyString())).thenReturn(FROM_FIND_BY_USER_NAME);
		Object result = sut.findByUserName(USERNAME_TO_FIND, mockDelegate);
		assertSame(FROM_FIND_BY_USER_NAME, result);
	}

	@Test
	void testUpdateByUserNameWithWrongUser() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		doThrow(NotAuthenticatedException.class).when(authorization).requireUsername(USERNAME_TO_UPDATE);
		try {
			sut.updateByUserName(USERNAME_TO_UPDATE, "XXX", mockDelegate);
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
		verify(mockDelegate, never()).accept(anyString(), anyString());
	}

	@Test
	void testUpdateByUserName() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		sut.updateByUserName(USERNAME_TO_UPDATE, CONTENT, mockDelegate);
		verify(authorization).requireUsername(USERNAME_TO_UPDATE);
		verify(mockDelegate).accept(USERNAME_TO_UPDATE, CONTENT);
	}

	@Test
	void testUpdateByIdWithoutUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireLogin();
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		try {
			sut.updateById(USER_ID_TO_UPDATE, "XXX", mockDelegate);
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
		verify(mockDelegate, never()).accept(anyString(), anyString());
	}

	@Test
	void testUpdateByIdWithOtherUser() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_OTHER);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		doThrow(NotAuthenticatedException.class).when(authorization).requireSystemUser();
		try {
			sut.updateById(USER_ID_TO_UPDATE, "XXX", mockDelegate);
			fail("should have thrown");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
		verify(mockDelegate, never()).accept(anyString(), anyString());
	}

	@Test
	void testUpdateByIdWithSystemUser() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		User user = new UserImpl(SYSTEM_USER_NAME, SYSTEM_USER_ID);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		doNothing().when(authorization).requireSystemUser();
		sut.updateById(USER_ID_TO_UPDATE, CONTENT, mockDelegate);
		verify(mockDelegate).accept(USER_ID_TO_UPDATE, CONTENT);
	}

	@Test
	void testUpdateByIdSameUser() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		User user = mock(User.class);
		when(user.getUniqueId()).thenReturn(USER_ID_TO_UPDATE);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);
		sut.updateById(USER_ID_TO_UPDATE, CONTENT, mockDelegate);
		verify(mockDelegate).accept(USER_ID_TO_UPDATE, CONTENT);
	}
}
