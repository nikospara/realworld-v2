package realworld.user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import realworld.user.dao.FollowDao;

/**
 * JPA implementation of the {@link FollowDao}.
 */
@ApplicationScoped
public class FollowDaoImpl implements FollowDao {

	private EntityManager em;

	/**
	 * Default constructors for the frameworks.
	 */
	FollowDaoImpl() {
		// NOOP
	}

	/**
	 * Full constructor for dependency injection.
	 *
	 * @param em The JPA entity manager
	 */
	@Inject
	public FollowDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public boolean exists(String followerId, String followedId) {
		FollowId followId = new FollowId(followerId, followedId);
		return em.find(FollowEntity.class, followId) != null;
	}

	@Override
	public void create(String followerId, String followedId) {
		FollowId followId = new FollowId(followerId, followedId);
		if( em.find(FollowEntity.class, followId) == null ) {
			FollowEntity follow = new FollowEntity();
			UserEntity follower = em.getReference(UserEntity.class, followerId);
			UserEntity followed = em.getReference(UserEntity.class, followedId);
			follow.setFollower(follower);
			follow.setFollowed(followed);
			em.persist(follow);
		}
	}

	@Override
	public int delete(String followerId, String followedId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<FollowEntity> criteriaDelete = cb.createCriteriaDelete(FollowEntity.class);
		Root<FollowEntity> followRoot = criteriaDelete.from(FollowEntity.class);
		criteriaDelete.where(cb.and(cb.equal(followRoot.get(FollowEntity_.follower).get(UserEntity_.id),followerId),cb.equal(followRoot.get(FollowEntity_.followed).get(UserEntity_.id),followedId)));
		return em.createQuery(criteriaDelete).executeUpdate();
	}

	@Override
	public List<String> findAllFollowed(String userId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<FollowEntity> followRoot = query.from(FollowEntity.class);
		query.select(followRoot.get(FollowEntity_.followed).get(UserEntity_.username))
				.where(cb.equal(followRoot.get(FollowEntity_.follower).get(UserEntity_.id), userId));
		return em.createQuery(query).getResultList();
	}

	@Override
	public Map<String, Boolean> checkAllFollowed(String userId, List<String> userNames) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<FollowEntity> followRoot = query.from(FollowEntity.class);
		query.select(followRoot.get(FollowEntity_.followed).get(UserEntity_.username))
				.where(cb.and(
						cb.equal(followRoot.get(FollowEntity_.follower).get(UserEntity_.id), userId),
						followRoot.get(FollowEntity_.followed).get(UserEntity_.username).in(userNames)
				));
		Map<String, Boolean> result = userNames.stream().collect(Collectors.toMap(Function.identity(), x -> false));
		em.createQuery(query).getResultStream().forEach(username -> result.put(username, true));
		return result;
	}
}
