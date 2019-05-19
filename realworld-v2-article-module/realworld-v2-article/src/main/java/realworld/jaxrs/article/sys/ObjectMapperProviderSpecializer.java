package realworld.jaxrs.article.sys;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import realworld.article.model.ArticleCombinedFullData;
import realworld.jaxrs.sys.ObjectMapperProvider;

/**
 * Customize Jackson's {@code ObjectMapper} through CDI Specialization.
 */
@ApplicationScoped
@Specializes
public class ObjectMapperProviderSpecializer extends ObjectMapperProvider {
	@PostConstruct
	void init() {
//		objectMapper.addMixIn(ArticleCombinedFullData.class, ArticleCombinedFullDataMixin.class);
	}
}
