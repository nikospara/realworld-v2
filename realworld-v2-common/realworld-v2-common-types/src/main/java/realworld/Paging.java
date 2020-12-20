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
