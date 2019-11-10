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
public class User {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "username")
	private String username;

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
