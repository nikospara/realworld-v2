package realworld.user.services.impl;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Security for the {@link realworld.user.services.FollowService} implementation.
 */
public interface FollowServiceAuthorizer {
	boolean follows(String followerName, String followedName, BiFunction<String,String,Boolean> delegate);

	void follow(String followerName, String followedName, BiConsumer<String,String> delegate);

	void unfollow(String followerName, String followedName, BiConsumer<String,String> delegate);

	List<String> findAllFollowed(String username, Function<String,List<String>> delegate);

	Map<String,Boolean> checkAllFollowed(String username, List<String> userNames, BiFunction<String,List<String>,Map<String,Boolean>> delegate);
}
