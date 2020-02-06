package realworld.article.model;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * An object holding data to update an article.
 */
public interface ArticleUpdateData {

	/**
	 * The id. Cannot be updated.
	 *
	 * @return The id.
	 */
	Optional<String> getId();

	/**
	 * The title.
	 *
	 * @return The title
	 */
	@Size(min=5)
	Optional<String> getTitle();

	/**
	 * The description.
	 *
	 * @return The description
	 */
	Optional<String> getDescription();

	/**
	 * Get the article body.
	 *
	 * @return The article body
	 */
	Optional<String> getBody();

	/**
	 * Get the tags for this article.
	 *
	 * @return The tags for this article
	 */
	Optional<Set<String>> getTagList();

	/**
	 * The author id. Only privileged users can change this field.
	 *
	 * @return The author id
	 */
	Optional<String> getAuthorId();

	/**
	 * The creation date. Only privileged users can change this field.
	 *
	 * @return the creation date
	 */
	Optional<LocalDateTime> getCreatedAt();

	/**
	 * The last modification date. Only privileged users can change this field.
	 *
	 * @return The last modification date
	 */
	Optional<LocalDateTime> getUpdatedAt();
}
