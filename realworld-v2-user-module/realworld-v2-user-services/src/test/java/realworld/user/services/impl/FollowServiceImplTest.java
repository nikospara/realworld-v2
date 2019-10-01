package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

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
	private FollowServiceAuthorizer authorizer;

	@Produces @Mock
	private FollowDao followDao;

	@Produces @Mock
	private UserService userService;

	@Inject
	private FollowServiceImpl sut;

	@Test
	void testFollows() {
		when(authorizer.follows(anyString(), anyString(), any())).thenAnswer(iom -> ((BiFunction<?,?,?>) iom.getArgument(2)).apply(iom.getArgument(0), iom.getArgument(1)));
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(userService.findByUserName(USERNAME2)).thenReturn(ImmutableUserData.builder().id(USERID2).username(USERNAME2).email("").build());
		sut.follows(USERNAME1,USERNAME2);
		verify(followDao).exists(USERID1,USERID2);
		verify(authorizer).follows(eq(USERNAME1), eq(USERNAME2), any());
	}

	@Test
	void testFollow() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).follow(anyString(), anyString(), any());
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(userService.findByUserName(USERNAME2)).thenReturn(ImmutableUserData.builder().id(USERID2).username(USERNAME2).email("").build());
		sut.follow(USERNAME1,USERNAME2);
		verify(followDao).create(USERID1,USERID2);
		verify(authorizer).follow(eq(USERNAME1), eq(USERNAME2), any());
	}

	@Test
	void testUnfollow() {
		doAnswer(iom -> {
			((BiConsumer<?,?>) iom.getArgument(2)).accept(iom.getArgument(0), iom.getArgument(1));
			return null;
		}).when(authorizer).unfollow(anyString(), anyString(), any());
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(userService.findByUserName(USERNAME2)).thenReturn(ImmutableUserData.builder().id(USERID2).username(USERNAME2).email("").build());
		sut.unfollow(USERNAME1,USERNAME2);
		verify(followDao).delete(USERID1,USERID2);
		verify(authorizer).unfollow(eq(USERNAME1), eq(USERNAME2), any());
	}

	@Test
	void testFindAllFollowed() {
		when(authorizer.findAllFollowed(anyString(), any())).thenAnswer(iom -> ((Function<?,?>) iom.getArgument(1)).apply(iom.getArgument(0)));
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(followDao.findAllFollowed(USERID1)).thenReturn(Collections.singletonList(USERNAME2));
		Object result = sut.findAllFollowed(USERNAME1);
		assertEquals(Collections.singletonList(USERNAME2), result);
		verify(authorizer).findAllFollowed(eq(USERNAME1), any());
	}

	@Test
	void testCheckAllFollowed() {
		when(authorizer.checkAllFollowed(anyString(), any(), any())).thenAnswer(iom -> ((BiFunction<?,?,?>) iom.getArgument(2)).apply(iom.getArgument(0), iom.getArgument(1)));
		when(userService.findByUserName(USERNAME1)).thenReturn(ImmutableUserData.builder().id(USERID1).username(USERNAME1).email("").build());
		when(followDao.checkAllFollowed(USERID1, Collections.singletonList(USERNAME2))).thenReturn(Collections.singletonMap(USERNAME2, true));
		Object result = sut.checkAllFollowed(USERNAME1, Collections.singletonList(USERNAME2));
		assertEquals(Collections.singletonMap(USERNAME2, true), result);
		verify(authorizer).checkAllFollowed(eq(USERNAME1), eq(Collections.singletonList(USERNAME2)), any());
	}
}
