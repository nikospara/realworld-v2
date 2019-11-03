package realworld.article.jaxrs.impl;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.jboss.resteasy.mock.MockHttpResponse;

/**
 * Assertions for the {@code SearchResult<ArticleSearchResultDto>}.
 */
public class ArticleSearchResultsDtoAssertions {
	private JsonObject data;
	private Iterator<JsonValue> resultsIterator;
	private JsonObject currentResult;

	static ArticleSearchResultsDtoAssertions assertSearchResultsDto(MockHttpResponse response) throws UnsupportedEncodingException {
		assertEquals(200, response.getStatus());
		JsonReader jsonReader = Json.createReader(new StringReader(response.getContentAsString()));
		JsonObject jobj = jsonReader.readObject();
		assertEquals(2, jobj.size());
		return new ArticleSearchResultsDtoAssertions(jobj);
	}

	private ArticleSearchResultsDtoAssertions(JsonObject data) {
		this.data = data;
	}

	ArticleSearchResultsDtoAssertions assertCount(int expected) {
		assertEquals(expected, data.getInt("count"));
		return this;
	}

	ArticleSearchResultsDtoAssertions nextResult() {
		if( resultsIterator == null ) {
			resultsIterator = data.getJsonArray("results").iterator();
		}
		currentResult = (JsonObject) resultsIterator.next();
		return this;
	}

	ArticleSearchResultsDtoAssertions assertId(String expected) {
		assertEquals(expected, currentResult.getString("id"));
		return this;
	}

	ArticleSearchResultsDtoAssertions assertTitle(String expected) {
		assertEquals(expected, currentResult.getString("title"));
		return this;
	}

	ArticleSearchResultsDtoAssertions assertSlug(String expected) {
		assertEquals(expected, currentResult.getString("slug"));
		return this;
	}

	ArticleSearchResultsDtoAssertions assertDescription(String expected) {
		assertEquals(expected, currentResult.getString("description"));
		return this;
	}

	ArticleSearchResultsDtoAssertions assertCreatedAt(String expected) {
		assertEquals(expected, currentResult.getString("createdAt"));
		return this;
	}

	ArticleSearchResultsDtoAssertions assertUpdatedAt(String expected) {
		assertEquals(expected, currentResult.getString("updatedAt"));
		return this;
	}

	ArticleSearchResultsDtoAssertions assertTagList(String... tags) {
		JsonArray jsonTagList = currentResult.getJsonArray("tagList");
		assertEquals(new HashSet<>(Arrays.asList(tags)), jsonTagList.getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(toSet()));
		return this;
	}
}
