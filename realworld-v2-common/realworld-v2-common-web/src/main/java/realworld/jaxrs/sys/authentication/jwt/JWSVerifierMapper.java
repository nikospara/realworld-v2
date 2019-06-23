package realworld.jaxrs.sys.authentication.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;

/**
 * Map from a string key id to the {@code JWSVerifier}, if any,
 * applying any caching policies etc.
 */
@FunctionalInterface
public interface JWSVerifierMapper {

	/**
	 * Do the mapping.
	 *
	 * @param kid The key id
	 * @return The corresponding {@code JWSVerifier}, if any
	 * @throws JOSEException On error
	 */
	JWSVerifier get(String kid) throws JOSEException;
}
