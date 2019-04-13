package realworld.user.persistence;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The user biography entity.
 *
 * <p>Its purpose is to keep the, potentially long, biography
 * field separated from the main {@link User} entity and loaded on demand for better
 * performance.</p>
 */
@Entity
@Table(name = "RWL_USER_BIO")
public class Biography {

	@Id
	private String userId;

	@MapsId
	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Lob
	@Column(name = "bio")
	private String bio;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
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
