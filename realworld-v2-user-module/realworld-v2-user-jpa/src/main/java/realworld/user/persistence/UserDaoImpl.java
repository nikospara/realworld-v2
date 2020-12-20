package realworld.user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.Optional;
import java.util.function.BiFunction;

import realworld.user.dao.UserDao;
import realworld.user.dao.UserUpdateOperation;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;

/**
 * DAO for the {@link UserEntity} entity.
 */
@ApplicationScoped
class UserDaoImpl implements UserDao {

	private EntityManager em;

	/**
	 * Default constructors for the frameworks.
	 */
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
	public UserData create(UserData user) {
		UserEntity u = new UserEntity();
		u.setId(user.getId());
		u.setUsername(user.getUsername());
		u.setEmail(user.getEmail());
		u.setImageUrl(user.getImageUrl());
		em.persist(u);
		return fromUser(u);
	}

	@Override
	public boolean usernameExists(String username) {
		return unique((cb, root) -> cb.equal(root.get(UserEntity_.username), username));
	}

	@Override
	public boolean emailExists(String email) {
		return unique((cb, root) -> cb.equal(cb.lower(root.get(UserEntity_.email)), email.toLowerCase()));
	}

	@Override
	public Optional<UserData> findByUserName(String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
		Root<UserEntity> root = query.from(UserEntity.class);
		query.where(cb.equal(root.get(UserEntity_.username), username));
		return em.createQuery(query).setMaxResults(1).getResultStream()
				.findFirst()
				.map(this::fromUser);
	}

	@Override
	public Optional<UserData> findByUserId(String id) {
		return Optional.ofNullable(em.find(UserEntity.class, id)).map(this::fromUser);
	}

	@Override
	public UserUpdateOperation createUpdate() {
		return new UserUpdateOperationImpl(em);
	}

	private boolean unique(BiFunction<CriteriaBuilder, Root<UserEntity>, Expression<Boolean>> callback) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<UserEntity> root = query.from(UserEntity.class);
		query.select(root.get(UserEntity_.username));
		query.where(callback.apply(cb, root));
		return !em.createQuery(query).setMaxResults(1).getResultList().isEmpty();
	}

	private UserData fromUser(UserEntity u) {
		return ImmutableUserData.builder().id(u.getId()).username(u.getUsername()).email(u.getEmail()).imageUrl(u.getImageUrl()).build();
	}
}
