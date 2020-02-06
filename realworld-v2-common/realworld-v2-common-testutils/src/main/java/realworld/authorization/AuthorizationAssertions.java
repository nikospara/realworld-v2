package realworld.authorization;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utilities to help testing authorization code.
 */
public class AuthorizationAssertions {
	public static void expectNotAuthenticatedException(Runnable f) {
		try {
			f.run();
			fail("expected NotAuthenticatedException");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}

	public static void expectNotAuthorizedException(Runnable f) {
		try {
			f.run();
			fail("expected NotAuthenticatedException");
		}
		catch( NotAuthorizedException expected ) {
			// expected
		}
	}
}
