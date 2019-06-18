package realworld.user.model;

import static realworld.json.DataConverterUtils.toCamelCase;
import static realworld.json.DataConverterUtils.toUpperSnakeCase;

import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Jackson converters for the {@link UserUpdateData}.
 */
class UserUpdateDataConverters {
	static class UserUpdateDataSerializationConverter extends StdConverter<UserUpdateData, Map<String,Object>> {
		@Override
		public Map<String, Object> convert(UserUpdateData value) {
			return value == null ? null : value.getProps().entrySet().stream().map(e -> Map.entry(toCamelCase(e.getKey()), e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	static class UserUpdateDataDeserializationConverter extends StdConverter<Map<String,Object>, UserUpdateData> {
		@Override
		public UserUpdateData convert(Map<String, Object> value) {
			if( value == null ) {
				return null;
			}
			else {
				UserUpdateData result = new UserUpdateData();
				value.forEach((key, value1) -> UserUpdateData.PropName.valueOf(toUpperSnakeCase(key)).set(result, value1));
				return result;
			}
		}
	}
}
