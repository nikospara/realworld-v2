package realworld.article.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.Optional;

import realworld.EntityDoesNotExistException;
import realworld.article.dao.UserDao;

/**
 * DAO implementation for the User entity using JPA.
 */
@ApplicationScoped
class UserDaoImpl implements UserDao {

	private EntityManager em;

	/**
	 * Default constructors for the frameworks.
	 */
	@SuppressWarnings("unused")
	UserDaoImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param em The JPA entity manager
	 */
	@Inject
	public UserDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public void add(String id, String username) {
		UserEntity u = new UserEntity();
		u.setId(id);
		u.setUsername(username);
		em.persist(u);
	}

	@Override
	public void updateUsername(String id, String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<UserEntity> updateQuery = cb.createCriteriaUpdate(UserEntity.class);
		Root<UserEntity> userRoot = updateQuery.from(UserEntity.class);
		updateQuery.set(UserEntity_.username, username);
		updateQuery.where(cb.equal(userRoot.get(UserEntity_.id), id));
		int count = em.createQuery(updateQuery).executeUpdate();
		if( count < 1 ) {
			throw new EntityDoesNotExistException();
		}
	}

	@Override
	public Optional<String> findByUserName(String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
		Root<UserEntity> root = query.from(UserEntity.class);
		query.where(cb.equal(root.get(UserEntity_.username), username));
		return em.createQuery(query).setMaxResults(1).getResultStream()
				.findFirst()
				.map(UserEntity::getId);
	}

	@Override
	public Optional<String> findByUserId(String id) {
		return Optional.ofNullable(em.find(UserEntity.class, id)).map(UserEntity::getUsername);
	}
}
