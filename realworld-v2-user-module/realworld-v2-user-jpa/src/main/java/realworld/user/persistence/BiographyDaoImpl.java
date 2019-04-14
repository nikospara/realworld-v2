package realworld.user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

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

	@Override
	public void update(String userId, String content) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Biography> updateQuery = cb.createCriteriaUpdate(Biography.class);
		Root<Biography> biographyRoot = updateQuery.from(Biography.class);
		updateQuery.set(Biography_.bio, content).where(cb.equal(biographyRoot.get(Biography_.userId), userId));
		em.createQuery(updateQuery).executeUpdate();
	}
}
