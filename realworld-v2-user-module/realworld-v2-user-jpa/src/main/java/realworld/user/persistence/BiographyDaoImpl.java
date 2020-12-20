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
 * DAO for the {@link BiographyEntity} entity.
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
		BiographyEntity b = new BiographyEntity();
		b.setUser(em.getReference(UserEntity.class, userId));
		b.setBio(content);
		em.persist(b);
	}

	@Override
	public void updateById(String userId, String content) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<BiographyEntity> updateQuery = cb.createCriteriaUpdate(BiographyEntity.class);
		Root<BiographyEntity> biographyRoot = updateQuery.from(BiographyEntity.class);
		updateQuery.set(BiographyEntity_.bio, content).where(cb.equal(biographyRoot.get(BiographyEntity_.userId), userId));
		em.createQuery(updateQuery).executeUpdate();
	}

	@Override
	public void updateByUserName(String username, String content) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<BiographyEntity> updateQuery = cb.createCriteriaUpdate(BiographyEntity.class);
		Root<BiographyEntity> biographyRoot = updateQuery.from(BiographyEntity.class);
		updateQuery.set(BiographyEntity_.bio, content).where(cb.equal(biographyRoot.get(BiographyEntity_.userId), userIdByNameSubquery(cb,updateQuery,username)));
		em.createQuery(updateQuery).executeUpdate();
	}

	@Override
	public Optional<String> findByUserName(String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<BiographyEntity> b = query.from(BiographyEntity.class);
		query.select(b.get(BiographyEntity_.bio)).where(cb.equal(b.get(BiographyEntity_.userId), userIdByNameSubquery(cb,query,username)));
		try {
			return Optional.of(em.createQuery(query).getSingleResult());
		}
		catch( NoResultException nre ) {
			return Optional.empty();
		}
	}

	private Expression<String> userIdByNameSubquery(CriteriaBuilder cb, CommonAbstractCriteria query, String username) {
		Subquery<String> subquery = query.subquery(String.class);
		Root<UserEntity> userRoot = subquery.from(UserEntity.class);
		subquery.select(userRoot.get(UserEntity_.id)).where(cb.equal(userRoot.get(UserEntity_.username),username));
		return subquery;
	}
}
