package realworld.jaxrs.sys.authentication.jwt;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import realworld.services.DateTimeService;

/**
 * Implementation of {@link JWSVerifierMapper}.
 * <p>
 * This will cache the retrieved data for a given amount of time.
 */
@ApplicationScoped
public class JWSVerifierMapperImpl implements JWSVerifierMapper {

	private DateTimeService dateTimeService;
	
	private TokenAuthenticationConfig tokenAuthenticationConfig;
	
	private RemoteJWKSet<? extends SecurityContext> jwkSet;

	/**
	 * Cache the verifiers by key id; the {@code RSASSAVerifier} <em>IS</em>
	 * thread-safe, as stated in its class Javadocs.
	 * No such guarantee is given for the interface {@code JWSVerifier}.
	 */
	private ConcurrentMap<String, RSASSAVerifier> verifierMap;
	
	private AtomicLong lastRetrievedAt = new AtomicLong(0);

	private ReentrantLock lock = new ReentrantLock();
	
	
	/**
	 * Default constructor for CDI.
	 */
	@SuppressWarnings("unused")
	JWSVerifierMapperImpl() {
		// NO OP
	}
	
	/**
	 * Constructor for injection.
	 * 
	 * @param dateTimeService The {@code DateTimeService}.
	 */
	@Inject
	public JWSVerifierMapperImpl(DateTimeService dateTimeService, TokenAuthenticationConfig tokenAuthenticationConfig) {
		this.dateTimeService = dateTimeService;
		this.tokenAuthenticationConfig = tokenAuthenticationConfig;
	}
	
	@PostConstruct
	void initialize() {
		jwkSet = new RemoteJWKSet<>(tokenAuthenticationConfig.getJwkUrl(), new ResourceRetrieverImpl(), new JWKSetCacheImpl());
		verifierMap = new ConcurrentHashMap<>();
	}

	@Override
	public JWSVerifier get(String kid) throws JOSEException {
		expireCacheIfNeeded();
		try {
			return verifierMap.computeIfAbsent(kid, this::compute);
		}
		catch( JOSEExceptionWrapper wrapper ) {
			throw wrapper.getWrapped();
		}
	}

	private void expireCacheIfNeeded() {
		if( isExpired() ) {
			lock.lock();
			try {
				if( isExpired() ) {
					verifierMap.clear();
				}
			}
			finally {
				lock.unlock();
			}
		}
	}

	private boolean isExpired() {
		return dateTimeService.currentTimeMillis() - lastRetrievedAt.get() > tokenAuthenticationConfig.getJwkCacheMaxTtl();
	}
	
	private RSASSAVerifier compute(String kid) {
		try {
    		JWKMatcher matcher = new JWKMatcher.Builder().keyID(kid).build();
    		List<JWK> keys = jwkSet.get(new JWKSelector(matcher), null);
    		if( keys.size() > 1 ) {
    			throw new JOSEException("found " + keys.size() + " keys for kid=" + kid);
    		}
			RSASSAVerifier result = null;
    		if( keys.size() == 1 ) {
    			JWK jwk = keys.get(0);
    			if( !(jwk instanceof RSAKey) ) {
    				throw new JOSEException("the key " + kid + " is not an RSAKey");
    			}
    			result = new RSASSAVerifier((RSAKey) jwk);
    		}
    		return result; // If null, it is not entered in the Map (ConcurrentMap/ConcurrentHashMap specs), so we are OK for memory attacks
		}
		catch( JOSEException jose ) {
			throw new JOSEExceptionWrapper(jose);
		}
	}
	
	
	private class ResourceRetrieverImpl extends DefaultResourceRetriever {
		
		private Resource cachedResource;
		private ReentrantLock lock = new ReentrantLock();
		
		ResourceRetrieverImpl() {
			super();
		}

		@Override
		public Resource retrieveResource(URL url) throws IOException {
			// We assume that it is not possible to change the value for a given key id,
			// so a cached kid is valid forever.
			if( skipCache() ) {
				lock.lock();
				try {
					if( skipCache() ) {
						Resource retrievedResource = super.retrieveResource(url);
						if (retrievedResource != null) {
							cachedResource = retrievedResource;
							lastRetrievedAt.set(dateTimeService.currentTimeMillis());
						}
					}
				}
				finally {
					lock.unlock();
				}
			}
			return cachedResource;
		}

		private boolean skipCache() {
			return cachedResource == null || dateTimeService.currentTimeMillis() - lastRetrievedAt.get() > tokenAuthenticationConfig.getJwkCacheTtl();
		}
	}


	private class JWKSetCacheImpl implements JWKSetCache {

		private JWKSet jwkSet;

		@Override
		public void put(JWKSet jwkSet) {
			this.jwkSet = jwkSet;
		}

		@Override
		public JWKSet get() {
			if( isExpired() ) {
				jwkSet = null;
			}
			return jwkSet;
		}

		@Override
		public boolean requiresRefresh() {
			return isExpired();
		}
	}
	
	
	private static class JOSEExceptionWrapper extends RuntimeException {

		private static final long serialVersionUID = 1L;

		JOSEExceptionWrapper(JOSEException cause) {
			super(cause);
		}
		
		JOSEException getWrapped() {
			return (JOSEException) getCause();
		}
	}
}
