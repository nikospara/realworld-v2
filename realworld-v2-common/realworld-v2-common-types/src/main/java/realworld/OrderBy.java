package realworld;

import static realworld.OrderByDirection.ASC;

import java.util.Optional;

/**
 * Representation of a typed {@code ORDER BY} expression.
 *
 * @param <F> The enum of the available fields to order by
 */
public class OrderBy<F extends Enum<F>> {

	private F field;
	private OrderByDirection direction;

	public OrderBy() {
		// NOOP
	}

	public OrderBy(F field, OrderByDirection direction) {
		this.field = field;
		this.direction = direction;
	}

	public static final <F extends Enum<F>> OrderBy<F> from(Class<F> enumType, String field, String direction) {
		return Optional.ofNullable(field)
				.map(f -> Enum.valueOf(enumType, f))
				.map(fieldEnum -> new OrderBy<>(fieldEnum, Optional.ofNullable(direction).map(OrderByDirection::valueOf).orElse(ASC)))
				.orElse(null);
	}

	public F getField() {
		return field;
	}

	public void setField(F field) {
		this.field = field;
	}

	public OrderByDirection getDirection() {
		return direction;
	}

	public void setDirection(OrderByDirection direction) {
		this.direction = direction;
	}
}
