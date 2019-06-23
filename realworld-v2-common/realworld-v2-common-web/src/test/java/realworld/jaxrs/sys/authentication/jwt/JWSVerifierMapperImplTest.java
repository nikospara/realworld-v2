package realworld.jaxrs.sys.authentication.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.nimbusds.jose.JWSVerifier;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.services.MockDateTimeService;

/**
 * Tests for the {@link JWSVerifierMapperImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class JWSVerifierMapperImplTest {

	private static final String JWK = "{\"keys\":[{\"kid\":\"KEY_ID\",\"kty\":\"RSA\",\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\"szDn7y7GceSJgx0J_fspI0JHjuFbvnurNEJ4Z5VwdH8tZ5K8jOjZcAiDHhyE9_Mmd8BvSTPkFTwP21sYwWxtwKdhe9jpFRAgSfUOAMBI6KMfgRqvISI28Iw81wwDpDh19rpor73bpjo6KZSfIdxKQHGAYXGcArM6NSOdlhvwFWVPqo3qabn-dBdp9WlFt1AgIFZOnOu4CaVArvr3RXx9Jf6aQx__7TyUaykl233tQ3qT5YkgtesUHlfTkKFV-xjtwDk2yL64C-SY4e7RjIahx468rNHYkEesejJu1RFDIN37hNR2a8tV0yLq-Jy2CwqnLya1McHx3T_sauQ_5fdq3w\",\"e\":\"AQAB\",\"x5c\":[\"MIICoTCCAYkCBgFrO7IJ9jANBgkqhkiG9w0BAQsFADAUMRIwEAYDVQQDDAlyZWFsd29ybGQwHhcNMTkwNjA5MTAwMjMyWhcNMjkwNjA5MTAwNDEyWjAUMRIwEAYDVQQDDAlyZWFsd29ybGQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCzMOfvLsZx5ImDHQn9+ykjQkeO4Vu+e6s0QnhnlXB0fy1nkryM6NlwCIMeHIT38yZ3wG9JM+QVPA/bWxjBbG3Ap2F72OkVECBJ9Q4AwEjoox+BGq8hIjbwjDzXDAOkOHX2umivvdumOjoplJ8h3EpAcYBhcZwCszo1I52WG/AVZU+qjeppuf50F2n1aUW3UCAgVk6c67gJpUCu+vdFfH0l/ppDH//tPJRrKSXbfe1DepPliSC16xQeV9OQoVX7GO3AOTbIvrgL5Jjh7tGMhqHHjrys0diQR6x6Mm7VEUMg3fuE1HZry1XTIur4nLYLCqcvJrUxwfHdP+xq5D/l92rfAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAJ0A/PGJAGD4WWgyB031CEVD/TJRZDUpPt1wi683Fg8veqxVoVItz551R2NHmu9X/p9RUZrKs66suoyJdspom51LPNh/UIQ6dlv1befmR3PPBhT2LHnwc5La1uc/oMm1HFbqrAPWgOSQvIgKrgH/WGZiV6VjVpdFe82O0KwgkflF2SDUPXO5gxRG9gQ/wbOVG866sY9IVB1nSO8W3xyHDUx+xpDR/gYLPY43LokWgFPNOPcf3SIVVZGtS2y0QImDV3Ny/Dr0buUpAhDtuMOrmZJNOyDKLD48mxY71X9YGkor+RRD5XMYeZD+56TeZy4CWKPpjP1oI0PsJsxypM23YKU=\"],\"x5t\":\"ELmQEbFhm436DYM6F0fnSySP3Lw\",\"x5t#S256\":\"-CuOq2gfVIYeUmjfsLuGSf33xh-2JpYvo3EPBPKZxNQ\"}]}";

	@Produces
	private MockDateTimeService dateTimeService = new MockDateTimeService();

	@Produces @Mock
	private TokenAuthenticationConfig tokenAuthenticationConfig;

	@Inject
	private JWSVerifierMapperImpl sut;

	@Test
	void testInitializationAndCaching() throws Exception {
		lenient().when(tokenAuthenticationConfig.getJwkCacheTtl()).thenAnswer(x -> 5000L);     // do not fetch the keys more frequently than once every 5000ms
		lenient().when(tokenAuthenticationConfig.getJwkCacheMaxTtl()).thenAnswer(x -> 10000L); // expire the cache, i.e. fetch anew every 10000ms

		HttpURLConnection conn = mock(HttpURLConnection.class);
		when(conn.getInputStream()).thenAnswer(x -> new ByteArrayInputStream(JWK.getBytes()));
		when(conn.getResponseCode()).thenReturn(200);
		URLStreamHandler handler = new URLStreamHandler() {
			@Override protected URLConnection openConnection(URL u) throws IOException {
				return conn;
			}
		};
		when(tokenAuthenticationConfig.getJwkUrl()).thenReturn(new URL("http", "idm-host", -1, "jwk", handler));

		JWSVerifier v = sut.get("KEY_ID");
		assertNotNull(v);

		dateTimeService.setNow(10000L);
		v = sut.get("KEY_ID");
		assertNotNull(v);

		verify(conn, times(1)).getInputStream();

		dateTimeService.setNow(10001L);
		v = sut.get("KEY_ID");
		assertNotNull(v);

		verify(conn, times(2)).getInputStream();
	}
}
