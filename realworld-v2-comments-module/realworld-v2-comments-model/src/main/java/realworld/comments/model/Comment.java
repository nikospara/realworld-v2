package realworld.comments.model;

import java.time.LocalDateTime;

import org.immutables.value.Value;

/**
 * A user comment on an article.
 */
@Value.Immutable
public interface Comment {

	/**
	 * The id.
	 *
	 * @return The id.
	 */
	@Nullable
	String getId();

	/**
	 * The body.
	 *
	 * @return The body.
	 */
	String getBody();

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

	/**
	 * The author id.
	 *
	 * @return The author id.
	 */
	String getAuthorId();

	/**
	 * The article id.
	 *
	 * @return The article id.
	 */
	String getArticleId();
}
