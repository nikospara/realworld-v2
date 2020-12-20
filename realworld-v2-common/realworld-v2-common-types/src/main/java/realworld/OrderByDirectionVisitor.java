package realworld;

/**
 * Visitor pattern for the {@link OrderByDirection} enum.
 *
 * @param <A> The type of the argument
 * @param <R> The return type
 */
public interface OrderByDirectionVisitor<A,R> {
	/**
	 * Handle the ascending direction.
	 *
	 * @param arg The argument
	 * @return Anything
	 */
	R asc(A arg);

	/**
	 * Handle the descending direction.
	 *
	 * @param arg The argument
	 * @return Anything
	 */
	R desc(A arg);
}
