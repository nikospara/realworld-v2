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
		return em.find(Follow.class, followId) != null;
	}

	@Override
	public void create(String followerId, String followedId) {
		FollowId followId = new FollowId(followerId, followedId);
		if( em.find(Follow.class, followId) == null ) {
			Follow follow = new Follow();
			User follower = em.getReference(User.class, followerId);
			User followed = em.getReference(User.class, followedId);
			follow.setFollower(follower);
			follow.setFollowed(followed);
			em.persist(follow);
		}
	}

	@Override
	public int delete(String followerId, String followedId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Follow> criteriaDelete = cb.createCriteriaDelete(Follow.class);
		Root<Follow> followRoot = criteriaDelete.from(Follow.class);
		criteriaDelete.where(cb.and(cb.equal(followRoot.get(Follow_.follower).get(User_.id),followerId),cb.equal(followRoot.get(Follow_.followed).get(User_.id),followedId)));
		return em.createQuery(criteriaDelete).executeUpdate();
	}

	@Override
	public List<String> findAllFollowed(String userId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Follow> followRoot = query.from(Follow.class);
		query.select(followRoot.get(Follow_.followed).get(User_.username))
				.where(cb.equal(followRoot.get(Follow_.follower).get(User_.id), userId));
		return em.createQuery(query).getResultList();
	}

	@Override
	public Map<String, Boolean> checkAllFollowed(String userId, List<String> userNames) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Follow> followRoot = query.from(Follow.class);
		query.select(followRoot.get(Follow_.followed).get(User_.username))
				.where(cb.and(
						cb.equal(followRoot.get(Follow_.follower).get(User_.id), userId),
						followRoot.get(Follow_.followed).get(User_.username).in(userNames)
				));
		Map<String, Boolean> result = userNames.stream().collect(Collectors.toMap(Function.identity(), x -> false));
		em.createQuery(query).getResultStream().forEach(username -> result.put(username, true));
		return result;
	}
}
