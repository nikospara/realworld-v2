package realworld.comments.jaxrs;

import realworld.comments.model.CommentCreationData;

/**
 * DTO for the {@link CommentCreationData}.
 */
public class CommentCreationParam implements CommentCreationData {
	private String body;

	@Override
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
