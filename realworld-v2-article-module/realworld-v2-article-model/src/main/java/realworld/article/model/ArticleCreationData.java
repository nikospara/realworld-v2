package realworld.article.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Article creation data.
 */
public interface ArticleCreationData {

	/**
	 * The title.
	 *
	 * @return The title
	 */
	@NotNull
	@Size(min=5)
	String getTitle();

	/**
	 * The description.
	 *
	 * @return The description
	 */
	String getDescription();

	/**
	 * Get the article body.
	 *
	 * @return The article body
	 */
	String getBody();

	/**
	 * Get the tags for this article.
	 *
	 * @return The tags for this article
	 */
	Set<String> getTagList();

	/**
	 * Get the author id.
	 *
	 * @return The author id
	 */
	String getAuthorId();
}
