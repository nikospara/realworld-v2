package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
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
import realworld.article.dao.UserDao;

/**
 * Tests for the {@link UserServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	private static final String USERNAME = "USERNAME";
	private static final String USER_ID = "USER_ID";

	@Produces @Mock
	private UserServiceAuthorizer authorizer;

	@Produces @Mock
	private UserDao userDao;

	@Inject
	private UserServiceImpl sut;

	@Test
	void testAdd() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).add(eq(USER_ID), eq(USERNAME), any());
		sut.add(USER_ID, USERNAME);
		verify(userDao).add(USER_ID, USERNAME);
	}

	@Test
	void testUpdateUsername() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).updateUsername(eq(USER_ID), eq(USERNAME), any());
		sut.updateUsername(USER_ID, USERNAME);
		verify(userDao).updateUsername(USER_ID, USERNAME);
	}

	@Test
	void testFindByUserName() {
		when(authorizer.findByUserName(eq(USERNAME), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		when(userDao.findByUserName(USERNAME)).thenReturn(Optional.of(USER_ID));
		Optional<String> result = sut.findByUserName(USERNAME);
		assertEquals(USER_ID, result.get());
		verify(userDao).findByUserName(USERNAME);
	}

	@Test
	void testFindByUserId() {
		when(authorizer.findByUserId(eq(USER_ID), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		when(userDao.findByUserId(USER_ID)).thenReturn(Optional.of(USERNAME));
		Optional<String> result = sut.findByUserId(USER_ID);
		assertEquals(USERNAME, result.get());
		verify(userDao).findByUserId(USER_ID);
	}
}
