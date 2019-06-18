package realworld.json;

import java.util.ArrayList;

/**
 * Jackson data converter utilities.
 */
public abstract class DataConverterUtils {

	private DataConverterUtils() {
		// DISABLE INSTANTIATION
	}

	/**
	 * Convert an instance of an {@code enum} to camel case, following simple rules.
	 * <p>
	 * The rules are:
	 * <ol>
	 *     <li>Convert to lower case</li>
	 *     <li>Convert every letter following an underscore to upper case</li>
	 *     <li>And remove the underscore</li>
	 * </ol>
	 *
	 * @param e The enum to convert to camel case
	 * @return The converted identifier
	 */
	public static String toCamelCase(Enum<?> e) {
		var segments = e.name().split("_");
		for( int i=0; i < segments.length; i++ ) {
			String segment = segments[i];
			String segmentToLower = segment.toLowerCase();
			String transformedSegment = (i > 0) ? segment.substring(0,1) + segmentToLower.substring(1) : segmentToLower;
			segments[i] = transformedSegment;
		}
		return String.join("", segments);
	}

	/**
	 * Convert a camel case identifier to an upper case string representation of an {@code enum} instance.
	 *
	 * @param s The camel case identifier
	 * @return The string representation of an {@code enum} instance
	 */
	public static String toUpperSnakeCase(String s) {
		var result = new ArrayList<String>();
		int baseIndex = 0;
		for( int i=1; i < s.length(); i++ ) {
			if( Character.isUpperCase(s.charAt(i)) ) {
				result.add(s.substring(baseIndex,i).toUpperCase());
				baseIndex = i;
			}
		}
		if( baseIndex < s.length() ) {
			result.add(s.substring(baseIndex).toUpperCase());
		}
		return String.join("_", result);
	}
}
