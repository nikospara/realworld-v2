package realworld.user.services.impl;

import java.util.function.Consumer;
import java.util.function.Function;

import realworld.user.model.UserData;
import realworld.user.model.UserUpdateData;

/**
 * Security for the {@link realworld.user.services.UserService} implementation.
 */
public interface UserServiceAuthorizer {
	UserData register(UserUpdateData registrationData, Function<UserUpdateData, UserData> delegate);

	UserData findByUserName(String username, Function<String, UserData> delegate);

	void update(UserUpdateData userUpdateData, Consumer<UserUpdateData> delegate);
}
