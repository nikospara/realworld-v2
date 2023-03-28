package realworld.article.services.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import java.util.function.Function;

import com.github.slugify.Slugify;
import realworld.model.common.v1.FormattedText;

/**
 * Produce the slugifier.
 *
 * @see Slugifier
 */
@ApplicationScoped
class TextFunctionsProducer {

	private Function<String,String> slugifier;
	private Function<FormattedText,String> unformatter;

	@PostConstruct
	void init() {
		Slugify slugify = Slugify.builder().lowerCase(true).build();
		slugifier = slugify::slugify;
		unformatter = x -> x == null ? "" : x.toString(); // TODO Implement appropriately
	}

	@Produces
	@ApplicationScoped
	@Slugifier
	Function<String,String> getSlugifier() {
		return slugifier;
	}

	@Produces
	@ApplicationScoped
	@Unformatter
	Function<FormattedText,String> getUnformatter() {
		return unformatter;
	}
}
