package realworld.article.services.authz.impl;

import static org.mockito.Mockito.mock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import realworld.article.services.authz.ArticleAuthorization;
import realworld.authorization.service.Authorization;

/**
 * Produces mocks for the dependencies of the {@link ArticleServiceAuthorizer}.
 * The test cannot produce them itself, because that creates cyclic dependencies.
 */
@ApplicationScoped
public class ArticleServiceAuthorizerDependenciesProducer {
	private Authorization authorization;

	private ArticleAuthorization articleAuthorization;

	@Produces
	public Authorization getAuthorization() {
		if( authorization == null ) {
			authorization = mock(Authorization.class);
		}
		return authorization;
	}

	@Produces
	public ArticleAuthorization getArticleAuthorization() {
		if( articleAuthorization == null ) {
			articleAuthorization = mock(ArticleAuthorization.class);
		}
		return articleAuthorization;
	}
}
