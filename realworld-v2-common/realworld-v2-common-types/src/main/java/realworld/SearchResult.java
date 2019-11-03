package realworld;

import java.util.List;

/**
 * A container for search results.
 *
 * @param <T> The type of each result item
 */
public class SearchResult<T> {
	private long count;
	private List<T> results;

	public SearchResult() {
		// NOOP
	}

	public SearchResult(long count, List<T> results) {
		this.count = count;
		this.results = results;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}
}
