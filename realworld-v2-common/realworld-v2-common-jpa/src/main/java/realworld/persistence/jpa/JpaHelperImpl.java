package realworld.persistence.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;

import java.util.function.Function;

import realworld.OrderBy;
import realworld.Paging;

/**
 * Implementation of the {@link JpaHelper}.
 */
@ApplicationScoped
public class JpaHelperImpl implements JpaHelper {

	private EntityManager em;

	/**
	 * Injection constructor.
	 *
	 * @param em The persistence context
	 */
	@Inject
	public JpaHelperImpl(EntityManager em) {
		this.em = em;
	}

	/**
	 * Default constructor for frameworks.
	 */
	@SuppressWarnings("unused")
	JpaHelperImpl() {
		// NOOP
	}

	@Override
	public <T,O extends Enum<O>> TypedQuery<T> applyPaging(CriteriaBuilder cb, CriteriaQuery<T> query, Paging<O> paging, Function<O, Expression<?>> orderByMapper) {
		if( paging != null ) {
			applyOrderBy(cb, query, paging.getOrderBy(), orderByMapper);
			TypedQuery<T> typedQuery = em.createQuery(query);
			if( paging.getOffset() != null ) {
				typedQuery.setFirstResult(paging.getOffset());
			}
			if( paging.getLimit() != null ) {
				typedQuery.setMaxResults(paging.getLimit());
			}
			return typedQuery;
		}
		else {
			return em.createQuery(query);
		}
	}

	private <O extends Enum<O>> void applyOrderBy(CriteriaBuilder cb, CriteriaQuery<?> query, OrderBy<O> orderBy, Function<O,Expression<?>> orderByMapper) {
		if( orderBy != null && orderBy.getField() != null && orderBy.getDirection() != null ) {
			Expression<?> orderByExpr = orderByMapper.apply(orderBy.getField());
			orderBy.getDirection().apply(new JpaOrderByDirectionVisitor(cb, query), orderByExpr);
		}
	}
}
