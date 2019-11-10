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
		User u = new User();
		u.setId(id);
		u.setUsername(username);
		em.persist(u);
	}

	@Override
	public void updateUsername(String id, String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<User> updateQuery = cb.createCriteriaUpdate(User.class);
		Root<User> userRoot = updateQuery.from(User.class);
		updateQuery.set(User_.username, username);
		updateQuery.where(cb.equal(userRoot.get(User_.id), id));
		int count = em.createQuery(updateQuery).executeUpdate();
		if( count < 1 ) {
			throw new EntityDoesNotExistException();
		}
	}

	@Override
	public Optional<String> findByUserName(String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.equal(root.get(User_.username), username));
		return em.createQuery(query).setMaxResults(1).getResultStream()
				.findFirst()
				.map(User::getId);
	}

	@Override
	public Optional<String> findByUserId(String id) {
		return Optional.ofNullable(em.find(User.class, id)).map(User::getUsername);
	}
}
