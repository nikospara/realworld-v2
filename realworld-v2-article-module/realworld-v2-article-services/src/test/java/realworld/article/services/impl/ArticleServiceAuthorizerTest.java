package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledDecorators;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.services.ArticleService;
import realworld.authorization.service.Authorization;

/**
 * Tests for the {@link ArticleServiceAuthorizer}.
 */
@EnableAutoWeld
@AddBeanClasses(ArticleServiceAuthorizerTest.DummyArticleService.class)
@AddEnabledDecorators(ArticleServiceAuthorizer.class)
@ExtendWith(MockitoExtension.class)
public class ArticleServiceAuthorizerTest {

	private static final String SLUG_TO_FIND_COMBINED_DATA = "SLUG_TO_FIND_COMBINED_DATA";
	private static final ArticleCombinedFullData FROM_FIND_FULL_DATA_BY_SLUG = new ArticleCombinedFullData();

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private DummyArticleService dummy;

	@Test
	void testFindFullDataBySlug() {
		Object res = dummy.findFullDataBySlug(SLUG_TO_FIND_COMBINED_DATA);
		assertSame(FROM_FIND_FULL_DATA_BY_SLUG, res);
		verifyNoMoreInteractions(authorization);
	}

	@ApplicationScoped
	static class DummyArticleService implements ArticleService {

		@Override
		public ArticleCombinedFullData findFullDataBySlug(String slug) {
			if( slug != SLUG_TO_FIND_COMBINED_DATA ) {
				throw new IllegalArgumentException();
			}
			return FROM_FIND_FULL_DATA_BY_SLUG;
		}
	}
}
