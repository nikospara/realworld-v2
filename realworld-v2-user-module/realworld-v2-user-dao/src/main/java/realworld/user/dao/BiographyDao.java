package realworld.user.dao;

/**
 * DAO interface for the Biography entity.
 */
public interface BiographyDao {

	/**
	 * Create the biography entry for the user with the given id.
	 *
	 * @param userId  The user id
	 * @param content The biography content
	 */
	void create(String userId, String content);

	/**
	 * Update the biography entry for the user with the given id.
	 *
	 * @param userId  The user id
	 * @param content The biography content
	 */
	void update(String userId, String content);
}
