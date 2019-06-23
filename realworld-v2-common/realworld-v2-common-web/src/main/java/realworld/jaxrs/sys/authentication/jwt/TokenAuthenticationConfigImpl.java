package realworld.jaxrs.sys.authentication.jwt;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import java.net.URL;

import realworld.jaxrs.sys.authentication.jwt.TokenAuthenticationConfig;

/**
 * Implementation of {@link TokenAuthenticationConfig} using JEE resources.
 */
@ApplicationScoped
public class TokenAuthenticationConfigImpl implements TokenAuthenticationConfig {
	
	public static final String JWK_URL_KEY = "java:/jwk.url";
	public static final String USERNAME_FIELD_IN_JWT_KEY = "java:/jwt.map.userName";
	public static final String USERID_FIELD_IN_JWT_KEY = "java:/jwt.map.userId";

	@Resource(name=USERNAME_FIELD_IN_JWT_KEY)
	private String usernameFieldInJwt;

	@Resource(name=USERID_FIELD_IN_JWT_KEY)
	private String userIdFieldInJwt;

	@Resource(name=JWK_URL_KEY)
	private URL jwkUrl;

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
