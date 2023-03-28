package realworld.persistence.jpa;

import realworld.model.common.v1.AuthorId;
import realworld.model.common.v1.FormattedText;
import realworld.model.common.v1.StructuredText;
import realworld.model.common.v1.TagId;

/**
 * Helper that can transform between the model-specific value objects and the JPA-friendly types.
 */
public interface JpaValueHelper {
	String transform(FormattedText value);

	String transform(StructuredText value);

	String transform(AuthorId value);

	String transform(TagId value);
}
