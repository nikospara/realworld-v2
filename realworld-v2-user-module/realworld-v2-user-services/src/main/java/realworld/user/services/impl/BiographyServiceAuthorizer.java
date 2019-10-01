package realworld.user.services.impl;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Security for the {@link realworld.user.services.BiographyService} implementation.
 */
public interface BiographyServiceAuthorizer {
	void create(String userId, String content, BiConsumer<String,String> delegate);

	String findByUserName(String username, Function<String,String> delegate);

	void updateByUserName(String username, String content, BiConsumer<String,String> delegate);

	void updateById(String userId, String content, BiConsumer<String,String> delegate);
}
