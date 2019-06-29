package realworld.authentication;

import javax.enterprise.context.RequestScoped;

/**
 * Keeps the {@link AuthenticationContext} (if any) associated with the current web request (if any).
 */
@RequestScoped
public class RequestAuthenticationContextHolder {

	private AuthenticationContext authenticationContext;

	/**
	 * Get and produce the {@code AuthenticationContext}.
	 *
	 * @return The current {@code AuthenticationContext}
	 */
	public AuthenticationContext getAuthenticationContext() {
		return authenticationContext;
	}

	/**
	 * Set the current {@code AuthenticationContext}.
	 * 
	 * @param authenticationContext The current {@code AuthenticationContext}
	 */
	public void setAuthenticationContext(AuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}
}
