package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;

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
	private UserDao userDao;

	@Inject
	private UserServiceImpl sut;

	@Test
	void testAdd() {
		sut.add(USER_ID, USERNAME);
		verify(userDao).add(USER_ID, USERNAME);
	}

	@Test
	void testUpdateUsername() {
		sut.updateUsername(USER_ID, USERNAME);
		verify(userDao).updateUsername(USER_ID, USERNAME);
	}

	@Test
	void testFindByUserName() {
		when(userDao.findByUserName(USERNAME)).thenReturn(Optional.of(USER_ID));
		Optional<String> result = sut.findByUserName(USERNAME);
		assertEquals(USER_ID, result.get());
		verify(userDao).findByUserName(USERNAME);
	}

	@Test
	void testFindByUserId() {
		when(userDao.findByUserId(USER_ID)).thenReturn(Optional.of(USERNAME));
		Optional<String> result = sut.findByUserId(USER_ID);
		assertEquals(USERNAME, result.get());
		verify(userDao).findByUserId(USER_ID);
	}
}
