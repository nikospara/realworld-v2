package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.EntityDoesNotExistException;
import realworld.SimpleValidationException;
import realworld.authentication.AuthenticationContext;
import realworld.authentication.User;
import realworld.user.dao.UserDao;
import realworld.user.dao.UserUpdateOperation;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;
import realworld.user.model.UserUpdateData.PropName;
import realworld.user.services.BiographyService;

/**
 * Tests for the {@link UserServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	private static final String USERID1 = "USERID1";
	private static final String USERNAME1 = "USERNAME1";
	private static final String USERNAME2 = "USERNAME2";
	private static final String EMAIL1 = "email.one@here.com";
	private static final String EMAIL2 = "email.two@here.com";
	private static final String BIO1 = "BIO1";
	private static final String BIO2 = "BIO2";
	private static final String IMAGE_URL1 = "IMAGE1";
	private static final String IMAGE_URL2 = "IMAGE2";

	@Produces @Mock
	private UserServiceAuthorizer authorizer;

	@Produces @Mock
	private UserDao userDao;

	@Produces @Mock
	private BiographyService biographyService;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private UserServiceImpl sut;

	@Test
	void testRegisterWithExistingUsername() {
		when(authorizer.register(any(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		UserUpdateData registrationData = mock(UserUpdateData.class);
		when(registrationData.getUsername()).thenReturn(USERNAME1);
		when(userDao.usernameExists(USERNAME1)).thenReturn(true);

		assertDuplicateUsername(() -> sut.register(registrationData));
		verify(authorizer).register(eq(registrationData), any());
	}

	@Test
	void testRegisterWithExistingEmail() {
		when(authorizer.register(any(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		UserUpdateData registrationData = mock(UserUpdateData.class);
		when(registrationData.getEmail()).thenReturn(EMAIL1);
		when(userDao.emailExists(EMAIL1)).thenReturn(true);

		assertDuplicateEmail(() -> sut.register(registrationData));
		verify(authorizer).register(eq(registrationData), any());
	}

	@Test
	void testRegister() {
		when(authorizer.register(any(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		UserUpdateData registrationData = new UserUpdateData();
		registrationData.setId(USERID1);
		registrationData.setUsername(USERNAME1);
		registrationData.setEmail(EMAIL1);
		registrationData.setImageUrl(IMAGE_URL1);
		registrationData.setBio(BIO1);
		when(userDao.create(any(UserData.class))).then(x -> ImmutableUserData.builder().from(x.getArgument(0)).id(USERID1).build());

		UserData result = sut.register(registrationData);

		assertEquals(USERID1, result.getId());
		assertEquals(USERNAME1, result.getUsername());
		assertEquals(EMAIL1, result.getEmail());
		assertEquals(IMAGE_URL1, result.getImageUrl());
		verify(biographyService).create(USERID1, BIO1);
		verify(authorizer).register(eq(registrationData), any());
	}

	@Test
	void testFindByNameForNonExistingUser() {
		when(authorizer.findByUserName(anyString(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.empty());
		try {
			sut.findByUserName(USERNAME1);
			fail("should throw for non-existing user");
		}
		catch( EntityDoesNotExistException e ) {
			assertEquals(USERNAME1, e.getMessage());
		}
		verify(authorizer).findByUserName(eq(USERNAME1), any());
	}

	@Test
	void testFindByName() {
		when(authorizer.findByUserName(anyString(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		UserData userData = mock(UserData.class);
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(userData));
		UserData result = sut.findByUserName(USERNAME1);
		assertSame(userData, result);
		verify(authorizer).findByUserName(eq(USERNAME1), any());
	}

	private void mockUpdate() {
		doAnswer(iom -> {
			((Consumer<?>) iom.getArgument(1)).accept(iom.getArgument(0));
			return null;
		}).when(authorizer).update(any(), any());
	}

	@Test
	void testUpdateWithExistingUsername() {
		mockUpdate();
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(true);
		when(userUpdateData.getUsername()).thenReturn(USERNAME2);
		when(userDao.usernameExists(USERNAME2)).thenReturn(true);

		assertDuplicateUsername(() -> sut.update(userUpdateData));
		verify(authorizer).update(eq(userUpdateData), any());
	}

	@Test
	void testUpdateWithSameUsername() {
		mockUpdate();
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));
		UserUpdateOperation updateOp = mockUserUpdateOperation();
		when(userDao.createUpdate()).thenReturn(updateOp);

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(true);
		when(userUpdateData.getUsername()).thenReturn(USERNAME1);

		sut.update(userUpdateData);

		verify(updateOp).executeForId(USERID1);
		verify(biographyService, never()).updateById(any(), any());
		verify(authorizer).update(eq(userUpdateData), any());
	}

	@Test
	void testUpdateWithExistingEmail() {
		mockUpdate();
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(false);
		when(userUpdateData.isExplicitlySet(PropName.EMAIL)).thenReturn(true);
		when(userUpdateData.getEmail()).thenReturn(EMAIL2);
		when(userDao.emailExists(EMAIL2)).thenReturn(true);

		assertDuplicateEmail(() -> sut.update(userUpdateData));
		verify(authorizer).update(eq(userUpdateData), any());
	}

	@Test
	void testUpdateWithSameEmail() {
		mockUpdate();
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));
		UserUpdateOperation updateOp = mockUserUpdateOperation();
		when(userDao.createUpdate()).thenReturn(updateOp);

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(PropName.USERNAME)).thenReturn(false);
		when(userUpdateData.isExplicitlySet(PropName.EMAIL)).thenReturn(true);
		when(userUpdateData.getEmail()).thenReturn(EMAIL1);

		sut.update(userUpdateData);

		verify(updateOp).executeForId(USERID1);
		verify(biographyService, never()).updateById(any(), any());
		verify(authorizer).update(eq(userUpdateData), any());
	}

	@Test
	void testUpdate() {
		mockUpdate();
		UserData cu = mockCurrentUser();
		when(userDao.findByUserName(USERNAME1)).thenReturn(Optional.of(cu));
		UserUpdateOperation updateOp = mockUserUpdateOperation();
		when(userDao.createUpdate()).thenReturn(updateOp);

		UserUpdateData userUpdateData = mock(UserUpdateData.class);
		when(userUpdateData.isExplicitlySet(any(PropName.class))).thenReturn(true);
		when(userUpdateData.getUsername()).thenReturn(USERNAME2);
		when(userUpdateData.getEmail()).thenReturn(EMAIL2);
		when(userUpdateData.getBio()).thenReturn(BIO2);
		when(userUpdateData.getImageUrl()).thenReturn(IMAGE_URL2);

		sut.update(userUpdateData);

		verify(updateOp).setUsername(true, USERNAME2);
		verify(updateOp).setEmail(true, EMAIL2);
		verify(updateOp).setImageUrl(true, IMAGE_URL2);
		verify(updateOp).executeForId(USERID1);
		verify(biographyService).updateById(USERID1, BIO2);
		verify(authorizer).update(eq(userUpdateData), any());
	}

	private void assertDuplicateUsername(Runnable f) {
		try {
			f.run();
			fail("duplicate username should have been reported!");
		}
		catch( SimpleValidationException e ) {
			assertNotNull(e.getViolations());
			assertEquals(1, e.getViolations().size());
			assertEquals("username", e.getViolations().get(0).getFieldName());
			assertEquals("duplicate user name", e.getViolations().get(0).getMessage());
		}
	}

	private void assertDuplicateEmail(Runnable f) {
		try {
			f.run();
			fail("duplicate email should have been reported!");
		}
		catch( SimpleValidationException e ) {
			assertNotNull(e.getViolations());
			assertEquals(1, e.getViolations().size());
			assertEquals("email", e.getViolations().get(0).getFieldName());
			assertEquals("duplicate email", e.getViolations().get(0).getMessage());
		}
	}

	private UserData mockCurrentUser() {
		User user = mock(User.class, Mockito.withSettings().lenient());
		when(user.getName()).thenReturn(USERNAME1);
		when(user.getUniqueId()).thenReturn(USERID1);
		when(authenticationContext.getUserPrincipal()).thenReturn(user);

		return ImmutableUserData.builder().id(USERID1).username(USERNAME1).email(EMAIL1).imageUrl("").build();
	}

	private UserUpdateOperation mockUserUpdateOperation() {
		return mock(UserUpdateOperation.class, invocation -> {
			if( UserUpdateOperation.class.isAssignableFrom(invocation.getMethod().getReturnType()) ) {
				return invocation.getMock();
			}
			return null;
		});
	}
}
