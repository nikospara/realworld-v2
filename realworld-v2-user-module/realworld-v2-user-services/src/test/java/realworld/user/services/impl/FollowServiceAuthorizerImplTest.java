package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authorization.service.Authorization;

/**
 * Tests for the {@link FollowServiceAuthorizerImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class FollowServiceAuthorizerImplTest {

	private static final String USERNAME_OF_FOLLOWER = "USERNAME_OF_FOLLOWER";
	private static final String USERNAME_TO_FOLLOW = "USERNAME_TO_FOLLOW";
	private static final String USERNAME_TO_UNFOLLOW = "USERNAME_TO_UNFOLLOW";
	private static final String USERNAME_TO_CHECK = "USERNAME_TO_CHECK";
	private static final String USERNAME_TO_FIND_ALL = "USERNAME_TO_FIND_ALL";
	private static final String USERNAME_TO_CHECK_ALL = "USERNAME_TO_CHECK_ALL";
	private static final List<String> FROM_FIND_ALL_FOLLOWED = new ArrayList<>();
	private static final Map<String,Boolean> FROM_CHECK_ALL_FOLLOWED = new HashMap<>();

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private FollowServiceAuthorizerImpl sut;

	@Test
	void testFollows() {
		@SuppressWarnings("unchecked")
		BiFunction<String,String,Boolean> mockDelegate = mock(BiFunction.class);
		when(mockDelegate.apply(anyString(),anyString())).thenReturn(false);
		sut.follows(USERNAME_OF_FOLLOWER, USERNAME_TO_CHECK, mockDelegate);
		verify(authorization).requireUsername(USERNAME_OF_FOLLOWER);
		verify(mockDelegate).apply(USERNAME_OF_FOLLOWER, USERNAME_TO_CHECK);
	}

	@Test
	void testFollow() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		sut.follow(USERNAME_OF_FOLLOWER, USERNAME_TO_FOLLOW, mockDelegate);
		verify(authorization).requireUsername(USERNAME_OF_FOLLOWER);
		verify(mockDelegate).accept(USERNAME_OF_FOLLOWER, USERNAME_TO_FOLLOW);
	}

	@Test
	void testUnfollow() {
		@SuppressWarnings("unchecked")
		BiConsumer<String,String> mockDelegate = mock(BiConsumer.class);
		sut.unfollow(USERNAME_OF_FOLLOWER, USERNAME_TO_UNFOLLOW, mockDelegate);
		verify(authorization).requireUsername(USERNAME_OF_FOLLOWER);
		verify(mockDelegate).accept(USERNAME_OF_FOLLOWER, USERNAME_TO_UNFOLLOW);
	}

	@Test
	void testFindAllFollowed() {
		@SuppressWarnings("unchecked")
		Function<String,List<String>> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(anyString())).thenReturn(FROM_FIND_ALL_FOLLOWED);
		Object result = sut.findAllFollowed(USERNAME_TO_FIND_ALL, mockDelegate);
		verify(authorization).requireUsername(USERNAME_TO_FIND_ALL);
		assertSame(FROM_FIND_ALL_FOLLOWED, result);
		verify(mockDelegate).apply(USERNAME_TO_FIND_ALL);
	}

	@Test
	void testCheckAllFollowed() {
		@SuppressWarnings("unchecked")
		BiFunction<String,List<String>,Map<String,Boolean>> mockDelegate = mock(BiFunction.class);
		when(mockDelegate.apply(anyString(), any())).thenReturn(FROM_CHECK_ALL_FOLLOWED);
		Object result = sut.checkAllFollowed(USERNAME_TO_CHECK_ALL, Collections.emptyList(), mockDelegate);
		verify(authorization).requireUsername(USERNAME_TO_CHECK_ALL);
		assertSame(FROM_CHECK_ALL_FOLLOWED, result);
		verify(mockDelegate).apply(eq(USERNAME_TO_CHECK_ALL), eq(Collections.emptyList()));
	}
}
