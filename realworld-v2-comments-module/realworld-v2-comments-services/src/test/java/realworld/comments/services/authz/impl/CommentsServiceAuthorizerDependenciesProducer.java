package realworld.comments.services.authz.impl;

import static org.mockito.Mockito.mock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import realworld.authorization.service.Authorization;
import realworld.comments.services.authz.CommentsAuthorization;

/**
 * Produces mocks for the dependencies of the {@link CommentsServiceAuthorizerImpl}.
 * The test cannot produce them itself, because that creates cyclic dependencies.
 */
@ApplicationScoped
public class CommentsServiceAuthorizerDependenciesProducer {
	private Authorization authorization;

	private CommentsAuthorization commentsAuthorization;

	@Produces
	public Authorization getAuthorization() {
		if( authorization == null ) {
			authorization = mock(Authorization.class);
		}
		return authorization;
	}

	@Produces
	public CommentsAuthorization getCommentsAuthorization() {
		if( commentsAuthorization == null ) {
			commentsAuthorization = mock(CommentsAuthorization.class);
		}
		return commentsAuthorization;
	}
}
