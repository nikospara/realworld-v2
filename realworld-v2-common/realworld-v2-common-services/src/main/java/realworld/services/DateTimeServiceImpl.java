package realworld.services;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

/**
 * Implementation of {@link DateTimeService}.
 */
@ApplicationScoped
class DateTimeServiceImpl implements DateTimeService {

	@Override
	public LocalDateTime getNow() {
		return LocalDateTime.now();
	}

	@Override
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}
}
