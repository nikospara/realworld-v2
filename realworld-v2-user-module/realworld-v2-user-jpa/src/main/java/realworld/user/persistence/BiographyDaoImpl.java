package realworld.user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import realworld.user.dao.BiographyDao;

/**
 * DAO for the {@link Biography} entity.
 */
@ApplicationScoped
public class BiographyDaoImpl implements BiographyDao {

	private EntityManager em;

	/**
	 * Default constructors for the frameworks.
	 */
	BiographyDaoImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param em The JPA entity manager
	 */
	@Inject
	public BiographyDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public void create(String userId, String content) {
		Biography b = new Biography();
		b.setUser(em.getReference(User.class, userId));
		b.setBio(content);
		em.persist(b);
	}
}
