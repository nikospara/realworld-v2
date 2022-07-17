package realworld.v1.services.types;

import java.util.Optional;

import realworld.v1.model.Comment;

public interface CommentSearchResult {
	Comment getComment();

	/**
	 * Return if the current user is following the creator of the comment.
	 * If the call was made anonymously, the {@code Optional} is empty.
	 *
	 * @return If the current user is following the creator of the comment
	 */
	Optional<Boolean> isFollowingUser();
}
