package realworld.user.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import realworld.json.ObjectMapperUtils;

/**
 * Tests for the {@link UserUpdateData} and its converters.
 */
public class UserUpdateDataTest {
	@Test
	void testSerialization() throws Exception {
		UserUpdateData uud = new UserUpdateData();
		uud.setUsername("myuser");
		uud.setImageUrl("http://image.url/");
		ObjectMapper om = ObjectMapperUtils.createObjectMapper();
		ObjectMapperUtils.customize(om);
		String result = om.writeValueAsString(uud);
		assertEquals(52, result.length());
		assertTrue(result.contains("\"imageUrl\":\"http://image.url/\""));
		assertTrue(result.contains("\"username\":\"myuser\""));

		UserUpdateData x = om.readValue(result, UserUpdateData.class);
		assertEquals("http://image.url/", x.getImageUrl());
		assertEquals("myuser", x.getUsername());
		assertFalse(x.isExplicitlySet(UserUpdateData.PropName.ID));
		assertFalse(x.isExplicitlySet(UserUpdateData.PropName.EMAIL));
		assertFalse(x.isExplicitlySet(UserUpdateData.PropName.BIO));
	}
}
