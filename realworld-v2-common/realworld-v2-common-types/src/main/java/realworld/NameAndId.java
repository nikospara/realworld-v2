package realworld;

import java.io.Serializable;
import java.util.Objects;

/**
 * A class carrying the display name and id of an entity.
 */
public class NameAndId implements Serializable {

	private String id;

	private String name;

	/**
	 * Create an object having only id.
	 *
	 * @param id The id
	 * @return A {@code NameAndId} object with the given id
	 */
	public static NameAndId ofId(String id) {
		return new NameAndId(null, id);
	}

	/**
	 * Default constructor.
	 */
	public NameAndId() {
		// NOOP
	}

	/**
	 * Constructor from all data.
	 *
	 * @param name The display name
	 * @param id   The id
	 */
	public NameAndId(String name, String id) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Get the id.
	 *
	 * @return id The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id.
	 *
	 * @param id The new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the name.
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 *
	 * @param name The new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NameAndId nameAndId = (NameAndId) o;
		return Objects.equals(id, nameAndId.id) && Objects.equals(name, nameAndId.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
