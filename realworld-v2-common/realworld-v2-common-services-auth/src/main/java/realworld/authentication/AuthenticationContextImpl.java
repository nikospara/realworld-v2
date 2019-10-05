package realworld.authentication;

import javax.enterprise.inject.Vetoed;

/**
 * {@link AuthenticationContext} implementation.
 */
@Vetoed
public class AuthenticationContextImpl implements AuthenticationContext {

	public static final String SYSTEM_USER_ID = "00000000-0000-0000-0000-000000000000";
	public static final String SYSTEM_USER_NAME = "system";

	private UserImpl userPrincipal;
	
	/**
	 * Create an {@code AuthenticationContextImpl} for the unauthenticated case.
	 * 
	 * @return The authentication context for the unauthenticated case
	 */
	public static AuthenticationContextImpl unauthenticated() {
		return new AuthenticationContextImpl();
	}

	/**
	 * Create an {@code AuthenticationContextImpl} for the system user.
	 *
	 * @return The authentication context for the system user
	 */
	public static AuthenticationContextImpl system() {
		UserImpl systemPrincipal = new UserImpl(SYSTEM_USER_NAME, SYSTEM_USER_ID);
		return AuthenticationContextImpl.forUser(systemPrincipal);
	}

	/**
	 * Create an {@code AuthenticationContextImpl} for the given user.
	 * 
	 * @param userPrincipal The user
	 * @return The authentication context
	 */
	public static AuthenticationContextImpl forUser(UserImpl userPrincipal) {
		AuthenticationContextImpl result = new AuthenticationContextImpl();
		result.userPrincipal = userPrincipal;
		return result;
	}

	@Override
	public UserImpl getUserPrincipal() {
		return userPrincipal;
	}

	@Override
	public boolean isSystem() {
		return userPrincipal != null && SYSTEM_USER_ID.equals(userPrincipal.getUniqueId());
	}
}
