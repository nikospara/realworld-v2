package realworld.article.services.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import java.util.function.Function;

import com.github.slugify.Slugify;

/**
 * Produce the slugifier.
 *
 * @see Slugifier
 */
@ApplicationScoped
class SlugifierProducer {

	private Slugify slugify;
	private Function<String,String> slugifier;

	@PostConstruct
	void init() {
		slugify = new Slugify();
		slugifier = slugify::slugify;
	}

	@Produces
	@ApplicationScoped
	@Slugifier
	Function<String,String> getSlugifier() {
		return slugifier;
	}
}
