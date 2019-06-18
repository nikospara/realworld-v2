package realworld.user.model;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Data required for updating a user.
 */
@JsonSerialize(converter = UserUpdateDataConverters.UserUpdateDataSerializationConverter.class)
@JsonDeserialize(converter = UserUpdateDataConverters.UserUpdateDataDeserializationConverter.class)
public class UserUpdateData implements Serializable {

	private static final long serialVersionUID = 1L;

	private EnumMap<PropName, Object> props = new EnumMap<>(PropName.class);

	/**
	 * Used by {@link UserUpdateData#isExplicitlySet(PropName)} to specify properties
	 * of a {@code UserUpdateData} object that have been changed.
	 */
	public enum PropName {
		ID(String.class),
		USERNAME(String.class),
		EMAIL(String.class),
		PASSWORD(String.class),
		IMAGE_URL(String.class),
		BIO(String.class);

		private Class<?> type;

		/**
		 * Construct a typed instance.
		 *
		 * @param type The type of the property
		 */
		PropName(Class<?> type) {
			this.type = Objects.requireNonNull(type);
		}

		/**
		 * Set the property described by this object to the target {@code UserUpdateData} instance.
		 *
		 * @param target The target {@code UserUpdateData} instance
		 * @param value  The value to set
		 */
		public void set(UserUpdateData target, Object value) {
			if( value != null && !type.isInstance(value) ) {
				throw new ClassCastException("type " + value.getClass().getName() + " cannot be cast to " + type.getName());
			}
			target.props.put(this, value);
		}
	}

	/** Get the user id. */
	public String getId() {
		return (String) props.get(PropName.ID);
	}

	/** Set the user id. */
	public void setId(String id) {
		props.put(PropName.ID, id);
	}

	/** Get the user name. */
	@Size(min=5)
	public String getUsername() {
		return (String) props.get(PropName.USERNAME);
	}

	/** Set the username. */
	public void setUsername(String username) {
		props.put(PropName.USERNAME, username);
	}

	/** Get the user email. */
	@Pattern(regexp="^.+@.+\\.[a-z]+$", flags=CASE_INSENSITIVE)
	public String getEmail() {
		return (String) props.get(PropName.EMAIL);
	}

	/** Set the email. */
	public void setEmail(String email) {
		props.put(PropName.EMAIL, email);
	}

	/** Get the password. */
	@Size(min=5)
	public String getPassword() {
		return (String) props.get(PropName.PASSWORD);
	}

	/** Set the password. */
	public void setPassword(String password) {
		props.put(PropName.PASSWORD, password);
	}

	/** Get the image URL. */
	public String getImageUrl() {
		return (String) props.get(PropName.IMAGE_URL);
	}

	/** Set the image. */
	public void setImageUrl(String imageUrl) {
		props.put(PropName.IMAGE_URL, imageUrl);
	}

	/** Get the biography. */
	public String getBio() {
		return (String) props.get(PropName.BIO);
	}

	/** Set the biography. */
	public void setBio(String bio) {
		props.put(PropName.BIO, bio);
	}

	/**
	 * Check if the given property is explicitly set. If not its value should be disregarded.
	 *
	 * @param prop The property to check
	 * @return Whether the property is explicitly set and should not be disregarded
	 */
	public boolean isExplicitlySet(PropName prop) {
		return props.containsKey(prop);
	}

	/**
	 * Return a view of this object as an unmodifiable map, containing only
	 * the properties that are actually set, even if set to null.
	 *
	 * @return A map view of this object
	 */
	public Map<PropName, Object> getProps() {
		return Collections.unmodifiableMap(props);
	}
}
