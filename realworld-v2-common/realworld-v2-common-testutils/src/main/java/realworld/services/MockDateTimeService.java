package realworld.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Mock implementation of the {@link DateTimeService}, where you can explicitly set when "now" is.
 */
public class MockDateTimeService implements DateTimeService {

	private LocalDateTime now = LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.ofTotalSeconds(0));

	/**
	 * Set when "now" is.
	 *
	 * @param now The new "now"
	 */
	public void setNow(LocalDateTime now) {
		this.now = now;
	}

	/**
	 * Set "now" in milliseconds.
	 *
	 * @param now The new "now"
	 */
	public void setNow(long now) {
		this.now = Instant.ofEpochMilli(now).atZone(ZoneOffset.ofTotalSeconds(0)).toLocalDateTime();
	}

	@Override
	public LocalDateTime getNow() {
		return now;
	}

	@Override
	public long currentTimeMillis() {
		return now.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
	}
}
