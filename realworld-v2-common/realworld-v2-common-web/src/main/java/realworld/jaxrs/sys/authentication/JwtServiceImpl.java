package realworld.jaxrs.sys.authentication;

import javax.enterprise.context.ApplicationScoped;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

/**
 * Default implementation of the {@link JwtService}.
 *
 * @see <a href="https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac">example</a>
 */
@ApplicationScoped
@Deprecated
public class JwtServiceImpl implements JwtService {

	/**
	 * Default constructor for frameworks.
	 */
	public JwtServiceImpl() {
		// NOOP
	}

	@Override
	public String toToken(String uniqueId, String userName) {
		throw new IllegalStateException("deprecated");
	}

	@Override
	public String updateUser(String uniqueId, String userName, String currentToken) {
		throw new IllegalStateException("deprecated");
	}

	@Override
	public boolean verify(SignedJWT jwt) throws JOSEException {
		throw new IllegalStateException("deprecated");
	}
}
