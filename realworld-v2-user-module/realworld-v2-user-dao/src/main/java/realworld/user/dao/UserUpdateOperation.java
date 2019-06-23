package realworld.user.dao;

import realworld.EntityDoesNotExistException;

/**
 * Encapsulate an updateById on the User entity.
 */
public interface UserUpdateOperation {
	/**
	 * Update the user name.
	 *
	 * @param reallySet If false, this field will not be updated
	 * @param newValue  The new value
	 * @return This operation for chaining
	 */
	UserUpdateOperation setUsername(boolean reallySet, String newValue);

	/**
	 * Update the email.
	 *
	 * @param reallySet If false, this field will not be updated
	 * @param newValue  The new value
	 * @return This operation for chaining
	 */
	UserUpdateOperation setEmail(boolean reallySet, String newValue);

	/**
	 * Update the image URL.
	 *
	 * @param reallySet If false, this field will not be updated
	 * @param newValue  The new value
	 * @return This operation for chaining
	 */
	UserUpdateOperation setImageUrl(boolean reallySet, String newValue);

	/**
	 * Update the entity with the given id.
	 *
	 * @param id The id of the entity to updateById
	 * @throws EntityDoesNotExistException If no entry is actually updated (thus, the id is not found)
	 */
	void executeForId(String id);
}
