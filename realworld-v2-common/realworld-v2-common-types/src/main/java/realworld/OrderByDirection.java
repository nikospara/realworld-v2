package realworld;

/**
 * Direction (ascending or descending) for results ordering.
 */
public enum OrderByDirection {
	ASC {
		@Override
		public <A, R> R apply(OrderByDirectionVisitor<A, R> visitor, A arg) {
			return visitor.asc(arg);
		}
	},
	DESC {
		@Override
		public <A, R> R apply(OrderByDirectionVisitor<A, R> visitor, A arg) {
			return visitor.desc(arg);
		}
	};

	public abstract <A,R> R apply(OrderByDirectionVisitor<A, R> visitor, A arg);
}
