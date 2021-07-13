package realworld.article.services.authz.impl;

import static org.mockito.Mockito.mock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import realworld.authorization.service.Authorization;

/**
 * Produces mocks for the dependencies of the {@link UserServiceAuthorizer}.
 * The test cannot produce them itself, because that creates cyclic dependencies.
 */
@ApplicationScoped
public class UserServiceAuthorizerDependenciesProducer {
	private Authorization authorization;

	@Produces
	public Authorization getAuthorization() {
		if( authorization == null ) {
			authorization = mock(Authorization.class);
		}
		return authorization;
	}
}
