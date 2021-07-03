package realworld.comments.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Comment creation data.
 */
public interface CommentCreationData {
	/**
	 * Get the comment body.
	 *
	 * @return The comment body
	 */
	@NotNull
	@Size(min=5)
	String getBody();
}
