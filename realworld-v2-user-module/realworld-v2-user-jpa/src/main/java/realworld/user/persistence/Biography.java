package realworld.user.persistence;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The user biography entity.
 */
@Entity
@Table(name = "RWL_USER_BIO")
public class Biography {

	@Id
	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Lob
	@Column(name = "bio")
	private String bio;

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
}
