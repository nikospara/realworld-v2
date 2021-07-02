package realworld.comments.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.Iterator;

import org.jboss.resteasy.mock.MockHttpResponse;

public class CommentsSearchResultsDtoAssertions {
	private JsonObject data;
	private Iterator<JsonValue> resultsIterator;
	private JsonObject currentResult;

	static CommentsSearchResultsDtoAssertions commentsSearchResultsDto(MockHttpResponse response) throws Exception {
		assertEquals(200, response.getStatus());
		JsonReader jsonReader = Json.createReader(new StringReader(response.getContentAsString()));
		JsonObject jobj = jsonReader.readObject();
		assertEquals(2, jobj.size());
		return new CommentsSearchResultsDtoAssertions(jobj);
	}

	public CommentsSearchResultsDtoAssertions(JsonObject data) {
		this.data = data;
	}

	CommentsSearchResultsDtoAssertions assertCount(int expected) {
		assertEquals(expected, data.getInt("count"));
		return this;
	}

	CommentsSearchResultsDtoAssertions nextResult() {
		if( resultsIterator == null ) {
			resultsIterator = data.getJsonArray("results").iterator();
		}
		currentResult = (JsonObject) resultsIterator.next();
		return this;
	}

	CommentsSearchResultsDtoAssertions assertId(String expected) {
		assertEquals(expected, currentResult.getString("id"));
		return this;
	}

	CommentsSearchResultsDtoAssertions assertBody(String expected) {
		assertEquals(expected, currentResult.getString("body"));
		return this;
	}

	CommentsSearchResultsDtoAssertions assertCreatedAt(String expected) {
		assertEquals(expected, currentResult.getString("createdAt"));
		return this;
	}

	CommentsSearchResultsDtoAssertions assertUpdatedAt(String expected) {
		assertEquals(expected, currentResult.getString("updatedAt"));
		return this;
	}

	CommentsSearchResultsDtoAssertions assertAuthorId(String expected) {
		assertEquals(expected, currentResult.getString("authorId"));
		return this;
	}

	CommentsSearchResultsDtoAssertions assertArticleId(String expected) {
		assertEquals(expected, currentResult.getString("articleId"));
		return this;
	}
}
