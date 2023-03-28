package realworld.article.model.v1;

import java.util.Optional;

import realworld.OffsetAndLimit;
import realworld.OrderBy;
import realworld.model.common.v1.UserId;

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
	 * Get the author criterion.
	 *
	 * @return The author criterion
	 */
	AuthorCriterion getAuthorCriterion();

	/**
	 * Get the favorited by criterion.
	 *
	 * @return The favorited by
	 */
	Optional<UserId> getFavoritedBy();

	/**
	 * Get the optional order by directive.
	 *
	 * @return The optional order
	 */
	Optional<OrderBy<ArticleFields>> getOrderBy();
}
