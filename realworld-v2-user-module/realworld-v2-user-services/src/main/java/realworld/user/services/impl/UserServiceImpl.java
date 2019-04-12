package realworld.user.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;

import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.services.UserService;

/**
 * Implementation of {@link UserService}.
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {
	@Override
	public UserData register(@Valid UserRegistrationData registrationData) {
		ImmutableUserData userData = ImmutableUserData.builder()
				.username(registrationData.getUsername())
				.email(registrationData.getEmail())
				.imageUrl(registrationData.getImageUrl())
				.build();
		return userData;
	}
}
