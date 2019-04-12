package realworld.jaxrs.sys.authentication;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

/**
 * JWT-related services.
 */
public interface JwtService {

	/**
	 * Create a JWT from the given user object.
	 *
	 * @param userName The user name
	 * @param uniqueId The user unique id
	 * @return The token
	 */
	String toToken(String userName, String uniqueId);

	/**
	 * Create a signed JWT that reflects the changes to the current user data,
	 * but has the same expiration time as the current token.
	 *
	 * @param userName The user name
	 * @param uniqueId The user unique id
	 * @param currentToken The current token
	 * @return The updated token
	 */
	String updateUser(String userName, String uniqueId, String currentToken);

	/**
	 * Verify the given JWT.
	 *
	 * @param jwt The JWT to verify
	 * @return Whether the given JWT verifies successfully
	 * @throws JOSEException On error
	 */
	boolean verify(SignedJWT jwt) throws JOSEException;
}
