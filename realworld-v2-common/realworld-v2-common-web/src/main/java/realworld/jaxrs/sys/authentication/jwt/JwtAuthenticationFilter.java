package realworld.jaxrs.sys.authentication.jwt;

import static javax.ws.rs.Priorities.AUTHENTICATION;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import realworld.authentication.RequestAuthenticationContextHolder;
import realworld.authentication.AuthenticationContextImpl;
import realworld.jaxrs.sys.authentication.JaxRsSecurityContextImpl;
import realworld.authentication.UserImpl;

/**
 * Extract the user info from the request and set the JAX-RS {@code SecurityContext}
 * as well as the {@link realworld.authentication.AuthenticationContext} of this
 * application.
 */
@Provider
@Priority(AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {
	
	private RequestAuthenticationContextHolder requestAuthenticationContextHolder;
	
	private TokenHelper tokenHelper;
	
	/**
	 * Public default constructor required by the infrastructure.
	 */
	public JwtAuthenticationFilter() {
		// NO OP
	}
	
	/**
	 * Injection constructor.
	 * 
	 * @param requestAuthenticationContextHolder The authentication context holder
	 * @param tokenHelper The token helper knows how to extract useful information from the token
	 */
	@Inject
	public JwtAuthenticationFilter(RequestAuthenticationContextHolder requestAuthenticationContextHolder, TokenHelper tokenHelper) {
		this.requestAuthenticationContextHolder = requestAuthenticationContextHolder;
		this.tokenHelper = tokenHelper;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		String token = tokenHelper.extractRawToken(requestContext);
		if( token != null ) {
			UserImpl user = tokenHelper.processToken(token);
			requestContext.setSecurityContext(new JaxRsSecurityContextImpl(user, requestContext.getSecurityContext().isSecure()));
			requestAuthenticationContextHolder.setAuthenticationContext(AuthenticationContextImpl.forUser(user));
		}
		else {
			requestAuthenticationContextHolder.setAuthenticationContext(AuthenticationContextImpl.unauthenticated());
		}
	}
}
