package realworld.article.model.v1;

import realworld.model.common.v1.TagId;

/**
 * Representation of a tag for an article.
 */
public interface Tag {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	TagId getId();

	/**
	 * The name of this tag.
	 *
	 * @return The name of this tag
	 */
	String getName();
}
