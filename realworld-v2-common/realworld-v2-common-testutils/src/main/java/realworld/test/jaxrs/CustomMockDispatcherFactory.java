package realworld.test.jaxrs;

import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Recreates the functionality of the {@code org.jboss.resteasy.mock.MockDispatcherFactory},
 * activating CDI and the application-specific providers.
 */
public class CustomMockDispatcherFactory {
	/**
	 * Factory method for the RestEasy {@code Dispatcher} required for running it in the tests.
	 *
	 * @param providers Providers to register
	 * @return The dispatcher
	 */
	public static Dispatcher createDispatcher(Class<?>... providers) {
		ResteasyProviderFactory providerFactory = new ResteasyProviderFactoryImpl();
		providerFactory.setInjectorFactory(new CdiInjectorFactory());
		for( Class<?> provider : providers ) {
			providerFactory.registerProvider(provider);
		}
		Dispatcher dispatcher = new SynchronousDispatcher(providerFactory);
		ResteasyProviderFactory.setInstance(providerFactory);
		RegisterBuiltin.register(providerFactory);
		return dispatcher;
	}
}
