package realworld.comments.services.authz;

/**
 * Comments module-specific methods for authorizing the access to comments services.
 */
public interface CommentsAuthorization {
	/**
	 * Authorize the deletion of the comment with the given id.
	 *
	 * @param id       The comment id
	 */
	void authorizeDelete(String id);
}
