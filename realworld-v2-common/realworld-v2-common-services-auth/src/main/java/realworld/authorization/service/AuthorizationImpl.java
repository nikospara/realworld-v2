package realworld.authorization.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import realworld.authentication.AuthenticationContext;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.NotAuthorizedException;

/**
 * Authorization implementation.
 */
@ApplicationScoped
class AuthorizationImpl implements Authorization {
	
	private AuthenticationContext authenticationContext;
	
	/**
	 * Default constructor required by the infrastructure.
	 */
	AuthorizationImpl() {
		// NO OP
	}

	/**
	 * Injection constructor.
	 * 
	 * @param authenticationContext The authentication context
	 */
	@Inject
	public AuthorizationImpl(AuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}

	@Override
	public void requireLogin() {
		if( authenticationContext.getUserPrincipal() == null ) {
			throw new NotAuthenticatedException();
		}
	}

	@Override
	public void requireUsername(String username) {
		if( authenticationContext.getUserPrincipal() == null ) {
			throw new NotAuthenticatedException();
		}
		if( !authenticationContext.getUserPrincipal().getName().equals(username) ) {
			throw new NotAuthorizedException();
		}
	}

	@Override
	public void requireUserId(String userId) {
		if( authenticationContext.getUserPrincipal() == null ) {
			throw new NotAuthenticatedException();
		}
		if( !authenticationContext.getUserPrincipal().getUniqueId().equals(userId) ) {
			throw new NotAuthorizedException();
		}
	}

	@Override
	public void requireSystemUser() {
		if( !authenticationContext.isSystem() ) {
			throw new NotAuthorizedException();
		}
	}
}
