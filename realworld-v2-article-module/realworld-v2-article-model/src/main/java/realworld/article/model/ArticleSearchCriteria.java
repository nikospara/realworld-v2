package realworld.article.model;

import java.util.List;

import org.immutables.value.Value;
import realworld.article.model.Nullable;

/**
 * Article search criteria.
 */
@Value.Immutable
public interface ArticleSearchCriteria {

	/**
	 * Get the tag.
	 *
	 * @return The tag
	 */
	@Nullable
	String getTag();

	/**
	 * Get the authors.
	 *
	 * @return The authors
	 */
	@Nullable
	List<String> getAuthors();

	/**
	 * Get the favorited by criterion.
	 *
	 * @return The favorited by
	 */
	@Nullable
	String getFavoritedBy();

	/**
	 * Get the result limit.
	 *
	 * @return The result limit
	 */
	@Nullable
	Integer getLimit();

	/**
	 * Get the starting result.
	 *
	 * @return The starting result
	 */
	@Nullable
	Integer getOffset();
}
