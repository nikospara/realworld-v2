package realworld.article.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RWL_ARTICLE_TAG")
public class TagEntity {
	@Id
	@Column(name = "name")
	private String name;

	/**
	 * Default constructor.
	 */
	public TagEntity() {
		// NOOP
	}

	/**
	 * Constructor from name.
	 *
	 * @param name The name
	 */
	public TagEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
