package realworld.user.model.events;

import realworld.user.model.UserUpdateData;

/**
 * A user modification event, fired when a user is created of updated.
 */
public class UserModificationEvent {

	private long timestamp;
	private String initiatingUserId;
	private UserModificationEventType type;
	private UserUpdateData payload;

	/** The timestamp of this event. */
	public long getTimestamp() {
		return timestamp;
	}

	/** The timestamp of this event. */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/** Id of the user that is responsible for firing this event. */
	public String getInitiatingUserId() {
		return initiatingUserId;
	}

	/** Id of the user that is responsible for firing this event. */
	public void setInitiatingUserId(String initiatingUserId) {
		this.initiatingUserId = initiatingUserId;
	}

	/** The event type. */
	public UserModificationEventType getType() {
		return type;
	}

	/** The event type. */
	public void setType(UserModificationEventType type) {
		this.type = type;
	}

	/** The event data. */
	public UserUpdateData getPayload() {
		return payload;
	}

	/** The event data. */
	public void setPayload(UserUpdateData payload) {
		this.payload = payload;
	}
}
