package realworld;

/**
 * Paging instructions for a query.
 *
 * @param <F> The enum that represents the field to order by
 */
public class Paging<F extends Enum<F>> {
	private Integer offset;
	private Integer limit;
	private OrderBy<F> orderBy;

	public static <F extends Enum<F>> Paging<F> of(Integer offset, Integer limit, OrderBy<F> orderBy) {
		if( offset == null && limit == null && orderBy == null ) {
			return null;
		}
		Paging<F> result = new Paging<>();
		result.setOffset(offset);
		result.setLimit(limit);
		result.setOrderBy(orderBy);
		return result;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public OrderBy<F> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy<F> orderBy) {
		this.orderBy = orderBy;
	}
}
