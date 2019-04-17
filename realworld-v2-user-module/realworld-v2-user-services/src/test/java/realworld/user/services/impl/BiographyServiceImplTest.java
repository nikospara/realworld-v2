package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
import realworld.EntityDoesNotExistException;
import realworld.user.dao.BiographyDao;

/**
 * Tests for the {@link BiographyServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class BiographyServiceImplTest {

	private static final String USERNAME = "USERNAME";
	private static final String USER_ID = "USER_ID";
	private static final String BIO = "BIO";

	@Produces @Mock
	private BiographyDao biographyDao;

	@Inject
	private BiographyServiceImpl sut;

	@Test
	void testFindByUserNameThrowsIfNotFound() {
		when(biographyDao.findByUserName(USERNAME)).thenReturn(Optional.empty());
		try {
			sut.findByUserName(USERNAME);
			fail("should throw if the user is not found");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
	}

	@Test
	void testFindByUserName() {
		when(biographyDao.findByUserName(USERNAME)).thenReturn(Optional.of(BIO));
		assertEquals(BIO, sut.findByUserName(USERNAME));
	}

	@Test
	void testUpdateByUserName() {
		sut.updateByUserName(USERNAME, BIO);
		verify(biographyDao).updateByUserName(USERNAME, BIO);
	}

	@Test
	void testUpdateById() {
		sut.updateById(USER_ID, BIO);
		verify(biographyDao).updateById(USER_ID, BIO);
	}
}
