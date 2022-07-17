package realworld.article.model;

import java.time.LocalDateTime;

import org.immutables.value.Value;
import realworld.Nullable;

/**
 * Base article, containing only its own fields, not related entities.
 */
@Value.Immutable
public interface ArticleBase {

	/**
	 * The id.
	 *
	 * @return The id.
	 */
	String getId();

	/**
	 * The slug.
	 *
	 * @return The slug
	 */
	String getSlug();

	/**
	 * The title.
	 *
	 * @return The title
	 */
	String getTitle();

	/**
	 * The description.
	 *
	 * @return The description
	 */
	String getDescription();

	/**
	 * The creation date.
	 *
	 * @return the creation date
	 */
	LocalDateTime getCreatedAt();

	/**
	 * The last modification date.
	 *
	 * @return The last modification date
	 */
	@Nullable
	LocalDateTime getUpdatedAt();
}
