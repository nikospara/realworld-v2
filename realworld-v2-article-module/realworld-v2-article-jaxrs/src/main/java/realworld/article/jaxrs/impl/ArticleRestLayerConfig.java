package realworld.article.jaxrs.impl;

/**
 * Configuration needed by the REST layer of the article module.
 */
public interface ArticleRestLayerConfig {

	/**
	 * The URL template used to create an {@code href} to a single user.
	 *
	 * @return The URL template
	 */
	String getUserUrlTemplate();
}
