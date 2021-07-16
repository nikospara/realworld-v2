package realworld.user.services.authz.impl;

import static org.mockito.Mockito.mock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import realworld.authentication.AuthenticationContext;
import realworld.authorization.service.Authorization;

/**
 * Produces mocks for the dependencies of the {@link BiographyServiceAuthorizer}.
 * The test cannot produce them itself, because that creates cyclic dependencies.
 */
@ApplicationScoped
public class AuthorizerDependenciesProducer {
	private Authorization authorization;

	private AuthenticationContext authenticationContext;

	@Produces
	public Authorization getAuthorization() {
		if( authorization == null ) {
			authorization = mock(Authorization.class);
		}
		return authorization;
	}

	@Produces
	public AuthenticationContext getAuthenticationContext() {
		if( authenticationContext == null ) {
			authenticationContext = mock(AuthenticationContext.class);
		}
		return authenticationContext;
	}
}
