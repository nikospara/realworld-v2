package realworld.v1.services.types;

import realworld.v1.model.CommentBody;

public interface CommentCreationData {
	/**
	 * The comment body text.
	 *
	 * @return The comment body
	 */
	CommentBody getBody();
}
