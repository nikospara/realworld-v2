package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
	private BiographyServiceAuthorizer authorizer;

	@Produces @Mock
	private BiographyDao biographyDao;

	@Inject
	private BiographyServiceImpl sut;

	@Test
	void testCreate() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).create(anyString(), anyString(), any());
		sut.create(USER_ID, BIO);
		verify(biographyDao).create(USER_ID, BIO);
		verify(authorizer).create(eq(USER_ID), eq(BIO), any());
	}

	@Test
	void testFindByUserNameThrowsIfNotFound() {
		when(authorizer.findByUserName(anyString(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		when(biographyDao.findByUserName(USERNAME)).thenReturn(Optional.empty());
		try {
			sut.findByUserName(USERNAME);
			fail("should throw if the user is not found");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
		verify(authorizer).findByUserName(eq(USERNAME), any());
	}

	@Test
	void testFindByUserName() {
		when(authorizer.findByUserName(anyString(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		when(biographyDao.findByUserName(USERNAME)).thenReturn(Optional.of(BIO));
		assertEquals(BIO, sut.findByUserName(USERNAME));
		verify(authorizer).findByUserName(eq(USERNAME), any());
	}

	@Test
	void testUpdateByUserName() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).updateByUserName(anyString(), anyString(), any());
		sut.updateByUserName(USERNAME, BIO);
		verify(biographyDao).updateByUserName(USERNAME, BIO);
		verify(authorizer).updateByUserName(eq(USERNAME), eq(BIO), any());
	}

	@Test
	void testUpdateById() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).updateById(anyString(), anyString(), any());
		sut.updateById(USER_ID, BIO);
		verify(biographyDao).updateById(USER_ID, BIO);
		verify(authorizer).updateById(eq(USER_ID), eq(BIO), any());
	}
}
