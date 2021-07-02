package realworld.comments.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Information for an article kept locally in the comments DB.
 * Essentially a map from slug to id.
 */
@Entity
@Table(name = "RWL_ARTICLE")
public class ArticleEntity {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "slug")
	private String slug;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
}
