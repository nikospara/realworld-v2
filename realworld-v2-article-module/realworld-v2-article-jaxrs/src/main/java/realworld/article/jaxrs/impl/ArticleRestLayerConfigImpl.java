package realworld.article.jaxrs.impl;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Implementation of {@link ArticleRestLayerConfig} with Microprofile config
 */
@ApplicationScoped
class ArticleRestLayerConfigImpl implements ArticleRestLayerConfig {

	@ConfigProperty(name="config.user.url-template")
	String userUrlTemplate;

	@Override
	public String getUserUrlTemplate() {
		return userUrlTemplate;
	}
}
