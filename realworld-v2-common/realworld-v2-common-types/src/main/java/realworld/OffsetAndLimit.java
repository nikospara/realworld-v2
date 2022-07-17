package realworld;

import java.util.OptionalInt;

/**
 * A trait that gives optional offset and limit to search criteria.
 */
public interface OffsetAndLimit {
	/**
	 * Get the result limit.
	 *
	 * @return The result limit
	 */
	OptionalInt getLimit();

	/**
	 * Get the starting result.
	 *
	 * @return The starting result
	 */
	OptionalInt getOffset();
}
