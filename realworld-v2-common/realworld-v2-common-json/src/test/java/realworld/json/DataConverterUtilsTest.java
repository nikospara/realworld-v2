package realworld.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link DataConverterUtils}.
 */
public class DataConverterUtilsTest {

	enum TestEnum {
		A_BOILED_EGG,
		SOMETHING,
		PRET_A_PORTER,
		VITAMINE_C
	}

	@Test
	void testToCamelCase() {
		assertEquals("aBoiledEgg", DataConverterUtils.toCamelCase(TestEnum.A_BOILED_EGG));
		assertEquals("something", DataConverterUtils.toCamelCase(TestEnum.SOMETHING));
		assertEquals("pretAPorter", DataConverterUtils.toCamelCase(TestEnum.PRET_A_PORTER));
		assertEquals("vitamineC", DataConverterUtils.toCamelCase(TestEnum.VITAMINE_C));
	}

	@Test
	void testToUpperSnakeCase() {
		assertEquals("A_BOILED_EGG", DataConverterUtils.toUpperSnakeCase("aBoiledEgg"));
		assertEquals("SOMETHING", DataConverterUtils.toUpperSnakeCase("something"));
		assertEquals("PRET_A_PORTER", DataConverterUtils.toUpperSnakeCase("pretAPorter"));
		assertEquals("VITAMINE_C", DataConverterUtils.toUpperSnakeCase("vitamineC"));
	}
}
