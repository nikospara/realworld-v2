package realworld.user.services.authz.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

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
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.user.services.FollowService;

/**
 * Tests for the {@link FollowServiceAuthorizer}.
 */
@EnableAutoWeld
@AddEnabledDecorators(FollowServiceAuthorizer.class)
@AddBeanClasses({FollowServiceAuthorizerTest.DummyFollowService.class, AuthorizerDependenciesProducer.class})
@ExtendWith(MockitoExtension.class)
public class FollowServiceAuthorizerTest {

	public interface SpyFollowService extends FollowService {
		String getLastCall();
	}

	public static class DummyFollowService implements SpyFollowService {
		private String lastCall;

		@Override
		public String getLastCall() {
			return lastCall;
		}

		@Override
		public boolean follows(String followerName, String followedName) {
			lastCall = LAST_CALL_FOLLOWS;
			return USERNAME_OF_FOLLOWER.equals(followerName) && USERNAME_TO_CHECK.equals(followedName);
		}

		@Override
		public void follow(String followerName, String followedName) {
			lastCall = LAST_CALL_FOLLOW;
		}

		@Override
		public void unfollow(String followerName, String followedName) {
			lastCall = LAST_CALL_UNFOLLOW;
		}

		@Override
		public List<String> findAllFollowed(String username) {
			return FROM_FIND_ALL_FOLLOWED;
		}

		@Override
		public Map<String, Boolean> checkAllFollowed(String username, List<String> userNames) {
			return FROM_CHECK_ALL_FOLLOWED;
		}
	}

	private static final String USERNAME_OF_FOLLOWER = "USERNAME_OF_FOLLOWER";
	private static final String USERNAME_TO_FOLLOW = "USERNAME_TO_FOLLOW";
	private static final String USERNAME_TO_UNFOLLOW = "USERNAME_TO_UNFOLLOW";
	private static final String USERNAME_TO_CHECK = "USERNAME_TO_CHECK";
	private static final String USERNAME_TO_FIND_ALL = "USERNAME_TO_FIND_ALL";
	private static final String USERNAME_TO_CHECK_ALL = "USERNAME_TO_CHECK_ALL";
	private static final List<String> FROM_FIND_ALL_FOLLOWED = new ArrayList<>();
	private static final Map<String,Boolean> FROM_CHECK_ALL_FOLLOWED = new HashMap<>();
	private static final String LAST_CALL_FOLLOWS = "LAST_CALL_FOLLOWS";
	private static final String LAST_CALL_FOLLOW = "LAST_CALL_FOLLOW";
	private static final String LAST_CALL_UNFOLLOW = "LAST_CALL_UNFOLLOW";

	@Inject
	private AuthorizerDependenciesProducer dependenciesProducer;

	@Inject
	private SpyFollowService sut;

	@Test
	void testFollows() {
		assertTrue(sut.follows(USERNAME_OF_FOLLOWER, USERNAME_TO_CHECK));
		verify(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_OF_FOLLOWER);
		assertEquals(LAST_CALL_FOLLOWS, sut.getLastCall());
	}

	@Test
	void testFollow() {
		sut.follow(USERNAME_OF_FOLLOWER, USERNAME_TO_FOLLOW);
		verify(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_OF_FOLLOWER);
		assertEquals(LAST_CALL_FOLLOW, sut.getLastCall());
	}

	@Test
	void testUnfollow() {
		sut.unfollow(USERNAME_OF_FOLLOWER, USERNAME_TO_UNFOLLOW);
		verify(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_OF_FOLLOWER);
		assertEquals(LAST_CALL_UNFOLLOW, sut.getLastCall());
	}

	@Test
	void testFindAllFollowed() {
		Object result = sut.findAllFollowed(USERNAME_TO_FIND_ALL);
		verify(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_TO_FIND_ALL);
		assertSame(FROM_FIND_ALL_FOLLOWED, result);
	}

	@Test
	void testCheckAllFollowed() {
		Object result = sut.checkAllFollowed(USERNAME_TO_CHECK_ALL, Collections.emptyList());
		verify(dependenciesProducer.getAuthorization()).requireUsername(USERNAME_TO_CHECK_ALL);
		assertSame(FROM_CHECK_ALL_FOLLOWED, result);
	}
}
