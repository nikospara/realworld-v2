package realworld.v1.services.types;

import java.util.List;
import java.util.Optional;

import realworld.OffsetAndLimit;
import realworld.v1.types.AuthorId;
import realworld.v1.types.UserId;
import realworld.v1.model.Tag;

/**
 * Article search criteria.
 */
public interface ArticleSearchCriteria extends OffsetAndLimit {
	/**
	 * Get the tag.
	 *
	 * @return The tag
	 */
	Optional<Tag> getTag();

	/**
	 * Get the authors.
	 *
	 * @return The authors
	 */
	List<AuthorId> getAuthors();

	/**
	 * Get the favorited by criterion.
	 *
	 * @return The favorited by
	 */
	Optional<UserId> getFavoritedBy();
}
