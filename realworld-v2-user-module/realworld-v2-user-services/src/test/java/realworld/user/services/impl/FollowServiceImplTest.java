package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.user.dao.FollowDao;
import realworld.user.model.ImmutableUserData;
import realworld.user.services.UserService;

/**
 * Tests for the {@link FollowServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class FollowServiceImplTest {

	private static final String USERID1 = "USERID1";
	private static final String USERID2 = "USERID2";
	private static final String USERNAME1 = "USERNAME1";
	private static final String USERNAME2 = "USERNAME2";

	@Produces @Mock
	private FollowDao followDao;

	@Produces @Mock
	private UserService userService;

	@Inject
	private FollowServiceImpl sut;

	@Test
	void testFollows() {
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(userService.findByUserName(USERNAME2)).thenReturn(ImmutableUserData.builder().id(USERID2).username(USERNAME2).email("").build());
		sut.follows(USERNAME1,USERNAME2);
		verify(followDao).exists(USERID1,USERID2);
	}

	@Test
	void testFollow() {
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(userService.findByUserName(USERNAME2)).thenReturn(ImmutableUserData.builder().id(USERID2).username(USERNAME2).email("").build());
		sut.follow(USERNAME1,USERNAME2);
		verify(followDao).create(USERID1,USERID2);
	}

	@Test
	void testUnfollow() {
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(userService.findByUserName(USERNAME2)).thenReturn(ImmutableUserData.builder().id(USERID2).username(USERNAME2).email("").build());
		sut.unfollow(USERNAME1,USERNAME2);
		verify(followDao).delete(USERID1,USERID2);
	}

	@Test
	void testFindAllFollowed() {
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(followDao.findAllFollowed(USERID1)).thenReturn(Collections.singletonList(USERNAME2));
		Object result = sut.findAllFollowed(USERNAME1);
		assertEquals(Collections.singletonList(USERNAME2), result);
	}

	@Test
	void testCheckAllFollowed() {
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(followDao.checkAllFollowed(USERID1, Collections.singletonList(USERNAME2))).thenReturn(Collections.singletonMap(USERNAME2, true));
		Object result = sut.checkAllFollowed(USERNAME1, Collections.singletonList(USERNAME2));
		assertEquals(Collections.singletonMap(USERNAME2, true), result);
	}
}
