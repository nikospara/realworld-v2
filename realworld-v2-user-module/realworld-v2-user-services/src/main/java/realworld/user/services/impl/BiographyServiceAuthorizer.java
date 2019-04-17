package realworld.user.services.impl;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import realworld.authorization.service.Authorization;
import realworld.user.services.BiographyService;

/**
 * Security for the {@link BiographyService} implementation.
 */
@Decorator
@Priority(APPLICATION)
public class BiographyServiceAuthorizer implements BiographyService {

	private BiographyService delegate;

	private Authorization authorization;

	/**
	 * Injection constructor.
	 *
	 * @param delegate      The delegate
	 * @param authorization The authorization
	 */
	@Inject
	public BiographyServiceAuthorizer(@Delegate BiographyService delegate, Authorization authorization) {
		this.delegate = delegate;
		this.authorization = authorization;
	}

	@Override
	public String findByUserName(String username) {
		return delegate.findByUserName(username);
	}

	@Override
	public void updateByUserName(String username, String content) {
		authorization.requireUsername(username);
		delegate.updateByUserName(username, content);
	}

	@Override
	public void updateById(String userId, String content) {
		authorization.requireUserId(userId);
		delegate.updateById(userId, content);
	}
}
