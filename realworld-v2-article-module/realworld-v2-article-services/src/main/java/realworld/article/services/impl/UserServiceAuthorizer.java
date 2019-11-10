package realworld.article.services.impl;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Security for the {@link realworld.article.services.UserService}.
 */
public interface UserServiceAuthorizer {
	/**
	 * Authorization for {@link realworld.article.services.UserService#add(String, String)}.
	 *
	 * @param id       The user id
	 * @param username The user name
	 * @param delegate The delegate
	 */
	void add(String id, String username, BiConsumer<String,String> delegate);

	/**
	 * Authorization for {@link realworld.article.services.UserService#updateUsername(String, String)}.
	 *
	 * @param id       The user id
	 * @param username The user name
	 * @param delegate The delegate
	 */
	void updateUsername(String id, String username, BiConsumer<String,String> delegate);

	/**
	 * Authorization for {@link realworld.article.services.UserService#findByUserName(String)}.
	 *
	 * @param username The user name
	 * @param delegate The delegate
	 * @return The return value of the delegate
	 */
	Optional<String> findByUserName(String username, Function<String, Optional<String>> delegate);

	/**
	 * Authorization for {@link realworld.article.services.UserService#findByUserId(String)}.
	 *
	 * @param id The user id
	 * @param delegate The delegate
	 * @return The return value of the delegate
	 */
	Optional<String> findByUserId(String id, Function<String, Optional<String>> delegate);
}
