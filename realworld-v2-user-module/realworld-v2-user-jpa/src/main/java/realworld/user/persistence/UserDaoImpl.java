package realworld.user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.UUID;

import realworld.user.dao.UserDao;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;

/**
 * DAO for the {@link User} entity.
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
	public UserData create(UserData user, String password) {
		User u = new User();
		u.setId(UUID.randomUUID().toString());
		u.setUsername(user.getUsername());
		u.setPassword(password);
		u.setEmail(user.getEmail());
		u.setImageUrl(user.getImageUrl());
		em.persist(u);
		return fromUser(u);
	}

	private UserData fromUser(User u) {
		return ImmutableUserData.builder().id(u.getId()).username(u.getUsername()).email(u.getEmail()).imageUrl(u.getImageUrl()).build();
	}
}
