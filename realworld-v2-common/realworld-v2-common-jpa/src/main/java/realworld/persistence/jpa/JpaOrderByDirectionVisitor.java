package realworld.persistence.jpa;

import javax.enterprise.inject.Vetoed;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import java.util.ArrayList;
import java.util.List;

import realworld.OrderByDirectionVisitor;

/**
 * Add ordering to a JPA criteria query, respecting any existing ordering and doing nothing if the expression is {@code null}.
 */
@Vetoed
public class JpaOrderByDirectionVisitor implements OrderByDirectionVisitor<Expression<?>, Order> {

	private CriteriaBuilder cb;
	private CriteriaQuery<?> query;

	public JpaOrderByDirectionVisitor(CriteriaBuilder cb, CriteriaQuery<?> query) {
		this.cb = cb;
		this.query = query;
	}

	@Override
	public Order asc(Expression<?> arg) {
		return arg != null ? addOrder(cb.asc(arg)) : null;
	}

	@Override
	public Order desc(Expression<?> arg) {
		return arg != null ? addOrder(cb.desc(arg)) : null;
	}

	private Order addOrder(Order order) {
		List<Order> orderList = query.getOrderList();
		if( orderList.isEmpty() ) {
			query.orderBy(order);
		}
		else {
			orderList = new ArrayList<>(orderList);
			orderList.add(order);
			query.orderBy(orderList);
		}
		return order;
	}
}
