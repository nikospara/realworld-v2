package realworld;

/**
 * Signal that a requested entity does not exist.
 */
public class EntityDoesNotExistException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public EntityDoesNotExistException() {
	}

	/**
	 * Construct with a message.
	 *
	 * @param message The message
	 */
	public EntityDoesNotExistException(String message) {
		super(message);
	}
}
