package realworld.article.jaxrs.impl;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;

import org.jboss.resteasy.mock.MockHttpResponse;
import realworld.article.jaxrs.ArticleCombinedFullDataDto;

/**
 * Assertions for the {@link ArticleCombinedFullDataDto}.
 */
class ArticleCombinedFullDataDtoAssertions {
	private JsonObject data;

	static ArticleCombinedFullDataDtoAssertions assertDto(MockHttpResponse response) throws UnsupportedEncodingException {
		assertEquals(200, response.getStatus());
		JsonReader jsonReader = Json.createReader(new StringReader(response.getContentAsString()));
		JsonObject jobj = jsonReader.readObject();
		assertEquals(11, jobj.size());
		return new ArticleCombinedFullDataDtoAssertions(jobj);
	}

	private ArticleCombinedFullDataDtoAssertions(JsonObject data) {
		this.data = data;
	}

	ArticleCombinedFullDataDtoAssertions assertId(String expected) {
		assertEquals(expected, data.getString("id"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertTitle(String expected) {
		assertEquals(expected, data.getString("title"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertSlug(String expected) {
		assertEquals(expected, data.getString("slug"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertDescription(String expected) {
		assertEquals(expected, data.getString("description"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertCreatedAt(String expected) {
		assertEquals(expected, data.getString("createdAt"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertAuthorName(String expected) {
		assertEquals(expected, data.getJsonObject("author").getString("name"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertAuthorHref(String expected) {
		assertEquals(expected, data.getJsonObject("author").getString("href"));
		return this;
	}

	ArticleCombinedFullDataDtoAssertions assertTagList(String... tags) {
		JsonArray jsonTagList = data.getJsonArray("tagList");
		assertEquals(new HashSet<>(Arrays.asList(tags)), jsonTagList.getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(toSet()));
		return this;
	}
}
