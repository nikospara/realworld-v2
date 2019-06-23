package realworld.jaxrs.sys.authentication.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.time.Instant;
import java.time.ZoneId;

import com.nimbusds.jose.JWSVerifier;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.jaxrs.sys.authentication.UserImpl;
import realworld.services.DateTimeService;

/**
 * Tests for the {@link TokenHelperImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class TokenHelperImplTest {

	private static final String TOKEN_OTHER_ALG = "eyJhbGciOiJPVEhFUiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJrcTZhd0lUUm4xdlNadVBJMEtCNF92eVlMUVkzWlFpOFBTeEl5RWdtQkZJIn0.eyJqdGkiOiIxZTBmN2ZkOS1mOTk3LTQyYWMtYWU1YS1jYjQyNmNmNmUxNmIiLCJleHAiOjE1NjEyODY0ODgsIm5iZiI6MCwiaWF0IjoxNTYxMjg2MTg4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0Ojg1ODAvYXV0aC9yZWFsbXMvcmVhbHdvcmxkIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjNkNGU1YzgzLTYwOGItNDIxNi1hMzAyLWQwY2QwMWU0OTYwOCIsInR5cCI6IkJlYXJlciIsImF6cCI6InJlYWx3b3JsZCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjAxOGMzNGY4LThhNzItNGZhNS04ZmY3LWJmM2IxMjRjYTExOSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlNwb25nZWJvYiBTcXVhcmVwYW50cyIsInByZWZlcnJlZF91c2VybmFtZSI6ImJvYiIsIm9pZCI6ImJvYiIsImdpdmVuX25hbWUiOiJTcG9uZ2Vib2IiLCJmYW1pbHlfbmFtZSI6IlNxdWFyZXBhbnRzIiwiZW1haWwiOiJib2I0QGJpa2luaS1ib3R0b20uY29tIn0.ZwpwboA_pZk2eusAKN5TSsw0ZPejOdedSYKkTfmYudpvz2erUql0UPLYZSFzed0Ccva554aqDfHGB7rkCVBI44QB0rUSPY1MwLCQ76Nb3TPfAnH6Ml8YDvwsqq5w3wZKGzOBgR76covHwFR-ZZns0XokYzcym3OqJXSvi7aaoC7msMuCR23qz0DsLQSMsxT00AN2xpQEQl0T7jucvVp-7xLH6o0JTcCIWd9yJebWuTGmm6_FKxQ2o9-s7o8q8Gv3iwJUCMcG1L26VUK9OES3e6iL_cO07sOW1et7iJXAqdl8cSm7A7u1pjImiVYTmF8osrlkkmigXDdWQPuaOHdG-g";
	private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJrcTZhd0lUUm4xdlNadVBJMEtCNF92eVlMUVkzWlFpOFBTeEl5RWdtQkZJIn0.eyJqdGkiOiIxZTBmN2ZkOS1mOTk3LTQyYWMtYWU1YS1jYjQyNmNmNmUxNmIiLCJleHAiOjE1NjEyODY0ODgsIm5iZiI6MCwiaWF0IjoxNTYxMjg2MTg4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0Ojg1ODAvYXV0aC9yZWFsbXMvcmVhbHdvcmxkIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjNkNGU1YzgzLTYwOGItNDIxNi1hMzAyLWQwY2QwMWU0OTYwOCIsInR5cCI6IkJlYXJlciIsImF6cCI6InJlYWx3b3JsZCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjAxOGMzNGY4LThhNzItNGZhNS04ZmY3LWJmM2IxMjRjYTExOSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlNwb25nZWJvYiBTcXVhcmVwYW50cyIsInByZWZlcnJlZF91c2VybmFtZSI6ImJvYiIsIm9pZCI6ImJvYiIsImdpdmVuX25hbWUiOiJTcG9uZ2Vib2IiLCJmYW1pbHlfbmFtZSI6IlNxdWFyZXBhbnRzIiwiZW1haWwiOiJib2I0QGJpa2luaS1ib3R0b20uY29tIn0.ZwpwboA_pZk2eusAKN5TSsw0ZPejOdedSYKkTfmYudpvz2erUql0UPLYZSFzed0Ccva554aqDfHGB7rkCVBI44QB0rUSPY1MwLCQ76Nb3TPfAnH6Ml8YDvwsqq5w3wZKGzOBgR76covHwFR-ZZns0XokYzcym3OqJXSvi7aaoC7msMuCR23qz0DsLQSMsxT00AN2xpQEQl0T7jucvVp-7xLH6o0JTcCIWd9yJebWuTGmm6_FKxQ2o9-s7o8q8Gv3iwJUCMcG1L26VUK9OES3e6iL_cO07sOW1et7iJXAqdl8cSm7A7u1pjImiVYTmF8osrlkkmigXDdWQPuaOHdG-g";
	private static final String KEY_ID = "kq6awITRn1vSZuPI0KB4_vyYLQY3ZQi8PSxIyEgmBFI";
	private static final long TOKEN_EXPIRATION_TIME = 1561286488000L;

	@Produces @Mock
	private JWSVerifierMapper jwsVerifierMapper;

	@Produces @Mock
	private DateTimeService dateTimeService;

	@Produces @Mock
	private TokenAuthenticationConfig tokenAuthenticationConfig;

	@Inject
	private TokenHelperImpl sut;

	@Test
	void testExtractRawTokenFromRequestContext() {
		@SuppressWarnings("unchecked")
		MultivaluedMap<String, String> headersMap = mock(MultivaluedMap.class);
		when(headersMap.getFirst(TokenHelperImpl.AUTHORIZATION_HEADER)).thenReturn("token the_token", null);
		ContainerRequestContext reqctx = mock(ContainerRequestContext.class);
		when(reqctx.getHeaders()).thenReturn(headersMap);
		assertEquals("the_token", sut.extractRawToken(reqctx));
		// the header is null in the 2nd call
		assertNull(sut.extractRawToken(reqctx));
	}

	@Test
	void testExtractRawTokenFromHeaders() {
		@SuppressWarnings("unchecked")
		MultivaluedMap<String, String> headersMap = mock(MultivaluedMap.class);
		when(headersMap.getFirst(TokenHelperImpl.AUTHORIZATION_HEADER)).thenReturn("token the_token", null);
		HttpHeaders headers = mock(HttpHeaders.class);
		when(headers.getRequestHeaders()).thenReturn(headersMap);
		assertEquals("the_token", sut.extractRawToken(headers));
		// the header is null in the 2nd call
		assertNull(sut.extractRawToken(headers));
	}

	@Test
	void testOnlyAcceptsAlgorithmRS256() {
		try {
			sut.processToken(TOKEN_OTHER_ALG);
			fail("only RS256 should be supported");
		}
		catch(NotAuthorizedException expected) {
			assertTrue(expected.getMessage().startsWith("unsupported algorithm: "));
		}
	}

	@Test
	void testKidShouldBeKnown() {
		try {
			sut.processToken(TOKEN);
			fail("only known key ids should be permitted");
		}
		catch(NotAuthorizedException expected) {
			assertTrue(expected.getMessage().startsWith("unknown kid: "));
		}
	}

	@Test
	void testTokensThatFailToVerifyAreRejected() throws Exception {
		JWSVerifier verifier = mock(JWSVerifier.class);
		when(verifier.verify(any(), any(), any())).thenReturn(false);
		when(jwsVerifierMapper.get(KEY_ID)).thenReturn(verifier);
		try {
			sut.processToken(TOKEN);
			fail("should reject tokens that fail to verify");
		}
		catch(NotAuthorizedException expected) {
			assertTrue(expected.getMessage().startsWith("failed to verify JWT: "));
		}
	}

	@Test
	void testExpiredTokensAreRejected() throws Exception {
		JWSVerifier verifier = mock(JWSVerifier.class);
		when(verifier.verify(any(), any(), any())).thenReturn(true);
		when(jwsVerifierMapper.get(KEY_ID)).thenReturn(verifier);
		when(dateTimeService.getNow()).thenReturn(Instant.ofEpochMilli(TOKEN_EXPIRATION_TIME + 1).atZone(ZoneId.systemDefault()).toLocalDateTime());
		try {
			sut.processToken(TOKEN);
			fail("should reject expired tokens");
		}
		catch(NotAuthorizedException expected) {
			assertTrue(expected.getMessage().startsWith("JWT expired at"));
		}
	}

	@Test
	void testCorrectTokenProducesUser() throws Exception {
		when(tokenAuthenticationConfig.getUsernameFieldInJwt()).thenReturn("oid");
		when(tokenAuthenticationConfig.getUserIdFieldInJwt()).thenReturn("sub");
		JWSVerifier verifier = mock(JWSVerifier.class);
		when(verifier.verify(any(), any(), any())).thenReturn(true);
		when(jwsVerifierMapper.get(KEY_ID)).thenReturn(verifier);
		when(dateTimeService.getNow()).thenReturn(Instant.ofEpochMilli(TOKEN_EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toLocalDateTime());
		UserImpl user = sut.processToken(TOKEN);
		assertNotNull(user);
		assertEquals("bob", user.getName());
		assertEquals("3d4e5c83-608b-4216-a302-d0cd01e49608", user.getUniqueId());
	}
}
