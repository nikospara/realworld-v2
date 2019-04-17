package realworld.user.persistence;

import javax.enterprise.inject.Vetoed;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import realworld.EntityDoesNotExistException;
import realworld.user.dao.UserUpdateOperation;

/**
 * Implementation of {@link UserUpdateOperation}.
 */
@Vetoed
class UserUpdateOperationImpl implements UserUpdateOperation {

	private EntityManager em;
	private CriteriaBuilder cb;
	private CriteriaUpdate<User> updateQuery;
	private Root<User> userRoot;
	private boolean hasAnyChange = false;

	/**
	 * Create a User updateById operation with this {@code EntityManager}.
	 *
	 * @param em The {@code EntityManager}
	 */
	UserUpdateOperationImpl(EntityManager em) {
		this.em = em;
		cb = em.getCriteriaBuilder();
		updateQuery = cb.createCriteriaUpdate(User.class);
		userRoot = updateQuery.from(User.class);
	}

	@Override
	public UserUpdateOperation setUsername(boolean reallySet, String newValue) {
		if( reallySet ) {
			updateQuery.set(User_.username, newValue);
			hasAnyChange = true;
		}
		return this;
	}

	@Override
	public UserUpdateOperation setEmail(boolean reallySet, String newValue) {
		if( reallySet ) {
			updateQuery.set(User_.email, newValue);
			hasAnyChange = true;
		}
		return this;
	}

	@Override
	public UserUpdateOperation setImageUrl(boolean reallySet, String newValue) {
		if( reallySet ) {
			updateQuery.set(User_.imageUrl, newValue);
			hasAnyChange = true;
		}
		return this;
	}

	@Override
	public UserUpdateOperation setPassword(boolean reallySet, String newValue) {
		if( reallySet ) {
			updateQuery.set(User_.password, newValue);
			hasAnyChange = true;
		}
		return this;
	}

	@Override
	public void executeForId(String id) {
		if( hasAnyChange ) {
			updateQuery.where(cb.equal(userRoot.get(User_.id), id));
			int count = em.createQuery(updateQuery).executeUpdate();
			if( count < 1 ) {
				throw new EntityDoesNotExistException();
			}
		}
	}
}
