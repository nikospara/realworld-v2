package realworld.jaxrs.sys.authentication;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

/**
 * JWT-related services.
 */
@Deprecated
public interface JwtService {

	/**
	 * Create a JWT from the given user object.
	 *
	 * @param uniqueId The user unique id
	 * @param userName The user name
	 * @return The token
	 */
	String toToken(String uniqueId, String userName);

	/**
	 * Create a signed JWT that reflects the changes to the current user data,
	 * but has the same expiration time as the current token.
	 *
	 * @param uniqueId The user unique id
	 * @param userName The user name
	 * @param currentToken The current token
	 * @return The updated token
	 */
	String updateUser(String uniqueId, String userName, String currentToken);

	/**
	 * Verify the given JWT.
	 *
	 * @param jwt The JWT to verify
	 * @return Whether the given JWT verifies successfully
	 * @throws JOSEException On error
	 */
	boolean verify(SignedJWT jwt) throws JOSEException;
}
