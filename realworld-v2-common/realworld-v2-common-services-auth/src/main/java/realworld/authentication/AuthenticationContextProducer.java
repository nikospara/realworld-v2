package realworld.authentication;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Produce an {@link AuthenticationContext} having the appropriate principal.
 * Allow pushing {@code AuthenticationContext}s to a "run as" effect.
 */
@ApplicationScoped
public class AuthenticationContextProducer {

	private Instance<RequestAuthenticationContextHolder> requestAuthenticationContextHolderInstance;

	private static ThreadLocal<Deque<AuthenticationContext>> runAsAuthContexts = ThreadLocal.withInitial(ArrayDeque::new);

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	AuthenticationContextProducer() {
		// NOOP
	}

	@Inject
	public AuthenticationContextProducer(Instance<RequestAuthenticationContextHolder> requestAuthenticationContextHolderInstance) {
		this.requestAuthenticationContextHolderInstance = requestAuthenticationContextHolderInstance;
	}

	@Produces @ApplicationScoped
	public AuthenticationContext getAuthenticationContext() {
		return new AuthenticationContextProxy();
	}

	/**
	 * Push an authentication context in the stack, having the effect of
	 * "run as" the principal contained in the new context.
	 *
	 * @param ctx The context to push
	 */
	public void pushContext(AuthenticationContext ctx) {
		runAsAuthContexts.get().addFirst(ctx);
	}

	/**
	 * Pop the authentication context, effectively ending a "run as".
	 *
	 * @return The popped authentication context
	 */
	public AuthenticationContext popContext() {
		return runAsAuthContexts.get().removeFirst();
	}

	private class AuthenticationContextProxy implements AuthenticationContext {
		@Override
		public User getUserPrincipal() {
			return selectAuthenticationContext()
					.map(AuthenticationContext::getUserPrincipal)
					.orElse(null);
		}

		private Optional<AuthenticationContext> selectAuthenticationContext() {
			return Optional.ofNullable(runAsAuthContexts.get().peekFirst())
					.or(this::getRequestAuthenticationContext);
		}

		private Optional<AuthenticationContext> getRequestAuthenticationContext() {
			try {
				return Optional.of(requestAuthenticationContextHolderInstance.get().getAuthenticationContext());
			}
			catch( ContextNotActiveException cnae ) {
				return Optional.empty();
			}
		}
	}
}
