package realworld.jaxrs.sys.authentication.jwt;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import realworld.authentication.UserImpl;

/**
 * Token processing helper.
 */
public interface TokenHelper {

	/**
	 * Try to extract the token from the {@code ContainerRequestContext}.
	 * 
	 * @param requestContext The context
	 * @return The token or {@code null}
	 */
	String extractRawToken(ContainerRequestContext requestContext);

	/**
	 * Try to extract the token from the {@code HttpHeaders}.
	 *
	 * @param headers The headers
	 * @return The token or {@code null}
	 */
	String extractRawToken(HttpHeaders headers);

	/**
	 * Process the token to create a {@link UserImpl}.
	 * 
	 * @param token The token, cannot be {@code null}
	 * @return The user
	 */
	UserImpl processToken(String token);
}
