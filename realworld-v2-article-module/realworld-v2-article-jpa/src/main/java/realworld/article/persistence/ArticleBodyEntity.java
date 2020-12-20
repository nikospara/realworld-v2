package realworld.article.persistence;

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
 * The article body entity.
 *
 * <p>Its purpose is to keep the body of an article, which is normally long and not always needed,
 * separated from the main {@link ArticleEntity} entity and loaded on demand for better performance.</p>
 */
@Entity
@Table(name = "RWL_ARTICLE_BODY")
public class ArticleBodyEntity {

	@Id
	private String articleId;

	@MapsId
	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "article_id")
	private ArticleEntity article;

	@Column(name = "body")
	@Lob
	private String body;

	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public ArticleEntity getArticle() {
		return article;
	}
	public void setArticle(ArticleEntity article) {
		this.article = article;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
