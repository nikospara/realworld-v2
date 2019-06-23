package realworld.jaxrs.sys.authentication.jwt;

import java.net.URL;

/**
 * Configuration for token authentication.
 */
public interface TokenAuthenticationConfig {

	/**
	 * The default JWK cache maximum TTL, see {@link #getJwkCacheMaxTtl()}.
	 */
	long MAX_CACHE_TTL = 120 * 60 * 1000; // 2 hours

	/**
	 * The default JWK cache (minimum) TTL, see {@link #getJwkCacheTtl()}.
	 */
	long MIN_CACHE_TTL =   5 * 60 * 1000; // 5 minutes

	/**
	 * Unused in the current configuration.
	 */
	URL getJwkUrl();

	/**
	 * The field of the JWT that maps to the user name.
	 *
	 * @return The field of the JWT that maps to the user name.
	 */
	String getUsernameFieldInJwt();

	/**
	 * The field of the JWT that maps to the user id.
	 *
	 * @return The field of the JWT that maps to the user id
	 */
	String getUserIdFieldInJwt();

	/**
	 * The maximum time to live for the cached JWK; if last retrieval is before this time,
	 * the cache is discarded and the JWK fetched anew. This minimizes the window within
	 * which compromised keys are valid for an attack.
	 */
	default long getJwkCacheMaxTtl() {
		return MAX_CACHE_TTL;
	}

	/**
	 * The minimum time before requesting a new JWK (e.g. because a new key id was sent by
	 * a client). This stops denial of service attacks where the attacker sends non-existing
	 * kids and we are forced to fetch the JWK continuously. It also leaves a window of time
	 * where new, legitimate kids will not be acknowledged by the system.
	 */
	default long getJwkCacheTtl() {
		return MIN_CACHE_TTL;
	}
}
