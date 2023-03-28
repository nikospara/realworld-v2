package realworld.article.model.v1;

import java.util.List;
import java.util.Objects;

import realworld.model.common.v1.AuthorId;
import realworld.model.common.v1.UserId;

/**
 * The author search criterion.
 */
public sealed interface AuthorCriterion permits AuthorCriterion.NoCriterion, AuthorCriterion.AllFollowedAuthors, AuthorCriterion.AuthorIdList {
	/**
	 * Singleton to specify no criterion - don't care who is the author of the article.
	 */
	final class NoCriterion implements AuthorCriterion {
		private NoCriterion() {
			// INTENTIONALLY BLANK
		}

		public static final NoCriterion INSTANCE = new NoCriterion();

		@Override
		public boolean equals(Object other) {
			return other instanceof NoCriterion;
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}

	/**
	 * Criterion to search all followed authors of the given user.
	 */
	record AllFollowedAuthors(UserId userId) implements AuthorCriterion {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}

	/**
	 * Author is any with id from the given list.
	 */
	record AuthorIdList(List<AuthorId> authorIds) implements AuthorCriterion {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.visit(this);
		}
	}

	interface Visitor<T> {
		T visit(NoCriterion value);
		T visit(AllFollowedAuthors value);
		T visit(AuthorIdList value);
	}

	<T> T visit(Visitor<T> visitor);

	default NoCriterion noCriterion() {
		return NoCriterion.INSTANCE;
	}

	default AllFollowedAuthors allFollowedAuthors(UserId userId) {
		return new AllFollowedAuthors(userId);
	}

	default AuthorIdList authorIdList(List<AuthorId> authorIds) {
		Objects.requireNonNull(authorIds, "authorIds cannot be null");
		return new AuthorIdList(authorIds);
	}
}
