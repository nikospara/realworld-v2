package realworld.article.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The user entity.
 */
@Entity
@Table(name = "RWL_USER")
public class UserEntity {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "username")
	private String username;

	/**
	 * Default constructor.
	 */
	public UserEntity() {
		// NOOP
	}

	/**
	 * Constructor from all data.
	 *
	 * @param id       The id
	 * @param username The user name
	 */
	public UserEntity(String id, String username) {
		this.id = id;
		this.username = username;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
