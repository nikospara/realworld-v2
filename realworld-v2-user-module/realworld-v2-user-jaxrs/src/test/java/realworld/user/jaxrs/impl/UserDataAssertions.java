package realworld.user.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.jboss.resteasy.mock.MockHttpResponse;

/**
 * Assertions helper for the {@code UserData} object.
 */
class UserDataAssertions {
	private JsonObject userData;

	static UserDataAssertions assertUserData(MockHttpResponse response) throws UnsupportedEncodingException {
		assertEquals(200, response.getStatus());
		JsonReader jsonReader = Json.createReader(new StringReader(response.getContentAsString()));
		JsonObject jobj = jsonReader.readObject();
		assertEquals(4, jobj.size());
		return new UserDataAssertions(jobj);
	}

	UserDataAssertions(JsonObject userData) {
		this.userData = userData;
	}

	UserDataAssertions assertId(String id) {
		assertEquals(id, userData.getString("id"));
		return this;
	}

	UserDataAssertions assertUsername(String username) {
		assertEquals(username, userData.getString("username"));
		return this;
	}

	UserDataAssertions assertEmail(String email) {
		assertEquals(email, userData.getString("email"));
		return this;
	}

	UserDataAssertions assertImageUrl(String imageUrl) {
		JsonValue imageVal = userData.get("imageUrl");
		assertEquals(imageUrl, imageVal.getValueType() == JsonValue.ValueType.NULL ? null : userData.getString("imageUrl"));
		return this;
	}
}
