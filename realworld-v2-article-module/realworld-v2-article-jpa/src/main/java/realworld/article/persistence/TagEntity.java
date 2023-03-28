package realworld.article.persistence;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RWL_ARTICLE_TAG")
public class TagEntity {

	@Id
	@Column(name = "id")
	private String id;

	@Id
	@Column(name = "name")
	private String name;

	/**
	 * Default constructor.
	 */
	TagEntity() {
		// NOOP
	}

	/**
	 * Constructor from name.
	 *
	 * @param name The name
	 */
	public TagEntity(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagEntity tagEntity = (TagEntity) o;
		return Objects.equals(id, tagEntity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
