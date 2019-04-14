package realworld.user.model;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.EnumSet;

/**
 * Data required for updating a user.
 */
public class UserUpdateData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String email;
	private String password;
	private String imageUrl;
	private String bio;
	private EnumSet<PropName> explicitlySetProps = EnumSet.noneOf(PropName.class);

	/**
	 * Used by {@link UserUpdateData#isExplicitlySet(PropName)} to specify properties
	 * of a {@code UserUpdateData} object that have been changed.
	 */
	public enum PropName {
		USERNAME,
		EMAIL,
		PASSWORD,
		IMAGE_URL,
		BIO
	}

	/** Get the user name. */
	@Size(min=5)
	public String getUsername() {
		return username;
	}

	/** Set the username. */
	public void setUsername(String username) {
		explicitlySetProps.add(PropName.USERNAME);
		this.username = username;
	}

	/** Get the user email. */
	@Pattern(regexp="^.+@.+\\.[a-z]+$", flags=CASE_INSENSITIVE)
	public String getEmail() {
		return email;
	}

	/** Set the email. */
	public void setEmail(String email) {
		explicitlySetProps.add(PropName.EMAIL);
		this.email = email;
	}

	/** Get the password. */
	@Size(min=5)
	public String getPassword() {
		return password;
	}

	/** Set the password. */
	public void setPassword(String password) {
		explicitlySetProps.add(PropName.PASSWORD);
		this.password = password;
	}

	/** Get the image URL. */
	public String getImageUrl() {
		return imageUrl;
	}

	/** Set the image. */
	public void setImage(String imageUrl) {
		explicitlySetProps.add(PropName.IMAGE_URL);
		this.imageUrl = imageUrl;
	}

	/** Get the biography. */
	public String getBio() {
		return bio;
	}

	/** Set the biography. */
	public void setBio(String bio) {
		explicitlySetProps.add(PropName.BIO);
		this.bio = bio;
	}

	/**
	 * Check if the given property is explicitly set. If not its value should be disregarded.
	 *
	 * @param prop The property to check
	 * @return Whether the property is explicitly set and should not be disregarded
	 */
	public boolean isExplicitlySet(PropName prop) {
		return explicitlySetProps.contains(prop);
	}
}
