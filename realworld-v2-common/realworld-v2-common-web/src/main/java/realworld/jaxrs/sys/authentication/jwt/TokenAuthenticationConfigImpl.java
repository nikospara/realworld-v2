package realworld.jaxrs.sys.authentication.jwt;

import javax.enterprise.context.ApplicationScoped;
import java.net.URL;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Implementation of {@link TokenAuthenticationConfig} using JEE resources.
 */
@ApplicationScoped
public class TokenAuthenticationConfigImpl implements TokenAuthenticationConfig {
	
	public static final String JWK_URL_KEY = "config.jwk.url";
	public static final String USERNAME_FIELD_IN_JWT_KEY = "config.jwt.map.userName";
	public static final String USERID_FIELD_IN_JWT_KEY = "config.jwt.map.userId";

	@ConfigProperty(name=USERNAME_FIELD_IN_JWT_KEY)
	String usernameFieldInJwt;

	@ConfigProperty(name=USERID_FIELD_IN_JWT_KEY)
	String userIdFieldInJwt;

	@ConfigProperty(name=JWK_URL_KEY)
	URL jwkUrl;

	@Override
	public URL getJwkUrl() {
		return jwkUrl;
	}

	@Override
	public String getUsernameFieldInJwt() {
		return usernameFieldInJwt;
	}

	@Override
	public String getUserIdFieldInJwt() {
		return userIdFieldInJwt;
	}
}
