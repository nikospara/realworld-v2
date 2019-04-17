package realworld.user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.util.Optional;

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
	public void updateById(String userId, String content) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Biography> updateQuery = cb.createCriteriaUpdate(Biography.class);
		Root<Biography> biographyRoot = updateQuery.from(Biography.class);
		updateQuery.set(Biography_.bio, content).where(cb.equal(biographyRoot.get(Biography_.userId), userId));
		em.createQuery(updateQuery).executeUpdate();
	}

	@Override
	public void updateByUserName(String username, String content) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Biography> updateQuery = cb.createCriteriaUpdate(Biography.class);
		Root<Biography> biographyRoot = updateQuery.from(Biography.class);
		updateQuery.set(Biography_.bio, content).where(cb.equal(biographyRoot.get(Biography_.userId), userIdByNameSubquery(cb,updateQuery,username)));
		em.createQuery(updateQuery).executeUpdate();
	}

	@Override
	public Optional<String> findByUserName(String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Biography> b = query.from(Biography.class);
		query.select(b.get(Biography_.bio)).where(cb.equal(b.get(Biography_.userId), userIdByNameSubquery(cb,query,username)));
		try {
			return Optional.of(em.createQuery(query).getSingleResult());
		}
		catch( NoResultException nre ) {
			return Optional.empty();
		}
	}

	private Expression<String> userIdByNameSubquery(CriteriaBuilder cb, CommonAbstractCriteria query, String username) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<User> userRoot = subquery.from(User.class);
		subquery.select(userRoot.get(User_.id)).where(cb.equal(userRoot.get(User_.username),username));
		return subquery;
	}
}
