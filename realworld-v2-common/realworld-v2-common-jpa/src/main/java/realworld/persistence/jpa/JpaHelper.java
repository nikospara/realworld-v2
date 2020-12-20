package realworld.persistence.jpa;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import java.util.function.Function;

import realworld.Paging;

/**
 * Contains useful JPA utilities.
 */
public interface JpaHelper {
	/**
	 * Apply paging to a query, takes care of nulls.
	 *
	 * @param cb            The criteria builder
	 * @param query         The query to apply paging to
	 * @param paging        The paging object, can be {@code null}
	 * @param orderByMapper Mapper from an order by field to a JPA expression to use for sorting
	 * @param <T>           The type of the query
	 * @param <O>           The type of the order by
	 * @return A JPA {@code TypedQuery} with first and max results set as appropriate
	 */
	<T, O extends Enum<O>> TypedQuery<T> applyPaging(CriteriaBuilder cb, CriteriaQuery<T> query, Paging<O> paging, Function<O, Expression<?>> orderByMapper);
}
