package realworld.article.model.v1;

import realworld.model.common.v1.AuthorId;

/**
 * The author of an article, not necessarily a user of the system.
 */
public interface Author {
	/**
	 * The id.
	 *
	 * @return The id
	 */
	AuthorId getId();
}
