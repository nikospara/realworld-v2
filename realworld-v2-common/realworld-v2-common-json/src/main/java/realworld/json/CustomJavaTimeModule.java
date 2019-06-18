package realworld.json;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Customize the way Jackson serializes the {@code java.time.LocalDateTime}.
 */
class CustomJavaTimeModule extends SimpleModule {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct and customize behavior.
	 */
	CustomJavaTimeModule() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
		addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
		addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
	}
}
