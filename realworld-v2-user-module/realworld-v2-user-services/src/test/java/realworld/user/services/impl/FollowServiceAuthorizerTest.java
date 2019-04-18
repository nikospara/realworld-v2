package realworld.user.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.authorization.service.Authorization;
import realworld.user.services.FollowService;

/**
 * Tests for the {@link FollowServiceAuthorizer}.
 */
@EnableAutoWeld
@AddBeanClasses(FollowServiceAuthorizerTest.DummyFollowService.class)
@AddEnabledDecorators(FollowServiceAuthorizer.class)
@ExtendWith(MockitoExtension.class)
public class FollowServiceAuthorizerTest {

	private static final String USERNAME_OF_FOLLOWER = "USERNAME_OF_FOLLOWER";
	private static final String USERNAME_TO_FOLLOW = "USERNAME_TO_FOLLOW";
	private static final String USERNAME_TO_UNFOLLOW = "USERNAME_TO_UNFOLLOW";
	private static final String USERNAME_TO_CHECK = "USERNAME_TO_CHECK";
	private static final String USERNAME_TO_FIND_ALL = "USERNAME_TO_FIND_ALL";
	private static final String USERNAME_TO_CHECK_ALL = "USERNAME_TO_CHECK_ALL";
	private static final Object FROM_FOLLOW = new Object();
	private static final Object FROM_UNFOLLOW = new Object();
	private static final List<String> FROM_FIND_ALL_FOLLOWED = new ArrayList<>();
	private static final Map<String,Boolean> FROM_CHECK_ALL_FOLLOWED = new HashMap<>();

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private DummyFollowService dummy;

	@Test
	void testFollows() {
		dummy.follows(USERNAME_OF_FOLLOWER, USERNAME_TO_CHECK);
		verify(authorization).requireUsername(USERNAME_OF_FOLLOWER);
	}

	@Test
	void testFollow() {
		dummy.follow(USERNAME_OF_FOLLOWER, USERNAME_TO_FOLLOW);
		verify(authorization).requireUsername(USERNAME_OF_FOLLOWER);
		assertSame(FROM_FOLLOW, dummy.getLastOperation());
	}

	@Test
	void testUnfollow() {
		dummy.unfollow(USERNAME_OF_FOLLOWER, USERNAME_TO_UNFOLLOW);
		verify(authorization).requireUsername(USERNAME_OF_FOLLOWER);
		assertSame(FROM_UNFOLLOW, dummy.getLastOperation());
	}

	@Test
	void testFindAllFollowed() {
		Object result = dummy.findAllFollowed(USERNAME_TO_FIND_ALL);
		verify(authorization).requireUsername(USERNAME_TO_FIND_ALL);
		assertSame(FROM_FIND_ALL_FOLLOWED, result);
	}

	@Test
	void testCheckAllFollowed() {
		Object result = dummy.checkAllFollowed(USERNAME_TO_CHECK_ALL, Collections.emptyList());
		verify(authorization).requireUsername(USERNAME_TO_CHECK_ALL);
		assertSame(FROM_CHECK_ALL_FOLLOWED, result);
	}

	@ApplicationScoped
	static class DummyFollowService implements FollowService {

		private Object lastOperation;

		Object getLastOperation() {
			return lastOperation;
		}

		@Override
		public boolean follows(String followerName, String followedName) {
			if( followerName != USERNAME_OF_FOLLOWER || followedName != USERNAME_TO_CHECK ) {
				throw new IllegalArgumentException();
			}
			return false;
		}

		@Override
		public void follow(String followerName, String followedName) {
			if( followerName != USERNAME_OF_FOLLOWER || followedName != USERNAME_TO_FOLLOW ) {
				throw new IllegalArgumentException();
			}
			lastOperation = FROM_FOLLOW;
		}

		@Override
		public void unfollow(String followerName, String followedName) {
			if( followerName != USERNAME_OF_FOLLOWER || followedName != USERNAME_TO_UNFOLLOW ) {
				throw new IllegalArgumentException();
			}
			lastOperation = FROM_UNFOLLOW;
		}

		@Override
		public List<String> findAllFollowed(String username) {
			if( username != USERNAME_TO_FIND_ALL ) {
				throw new IllegalArgumentException();
			}
			return FROM_FIND_ALL_FOLLOWED;
		}

		@Override
		public Map<String, Boolean> checkAllFollowed(String username, List<String> userNames) {
			if( username != USERNAME_TO_CHECK_ALL ) {
				throw new IllegalArgumentException();
			}
			return FROM_CHECK_ALL_FOLLOWED;
		}
	}
}
