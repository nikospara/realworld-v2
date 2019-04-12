package realworld.authorization;

/**
 * No authentication information could be retrieved or authentication failed.
 */
public class NotAuthenticatedException extends AppSecurityException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public NotAuthenticatedException() {
		// NOOP
	}

	/**
	 * @param message
	 */
	public NotAuthenticatedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotAuthenticatedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotAuthenticatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
