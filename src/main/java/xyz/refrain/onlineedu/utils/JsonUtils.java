package xyz.refrain.onlineedu.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Json utilities.
 */
@Component
@DependsOn("objectMapper")
public class JsonUtils {

	private static ObjectMapper objectMapper;

	private static ObjectMapper createObjectMapper() {

		ObjectMapper om = new ObjectMapper();
		om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

		String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
		String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
		String DEFAULT_TIME_PATTERN = "HH:mm:ss";

		JavaTimeModule module = new JavaTimeModule();
		module
				.addSerializer(LocalDateTime.class,
						new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)))
				.addSerializer(LocalDate.class,
						new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)))
				.addSerializer(LocalTime.class,
						new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)))
				.addDeserializer(LocalDateTime.class,
						new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)))
				.addDeserializer(LocalDate.class,
						new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)))
				.addDeserializer(LocalTime.class,
						new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));

		om.registerModule(module);

		om.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN));

		return om;
	}

	@Autowired
	public JsonUtils(ObjectMapper objectMapper) {
		JsonUtils.objectMapper = objectMapper;
	}

	public static <T> T jsonToObject(@NonNull String json, @NonNull Class<T> type) throws IOException {
		Assert.hasText(json, "Json content must not be blank");
		Assert.notNull(type, "Target type must not be null");

		return objectMapper.readValue(json, type);
	}


	public static String objectToJson(@NonNull Object source) throws JsonProcessingException {
		Assert.notNull(source, "Source object must not be null");

		return objectMapper.writeValueAsString(source);
	}


	public static <T> T mapToObject(@NonNull Map<String, ?> sourceMap, @NonNull Class<T> type) throws IOException {
		Assert.notEmpty(sourceMap, "Source map must not be empty");

		// Serialize the map
		String json = objectToJson(sourceMap);

		// Deserialize the json format of the map
		return jsonToObject(json, type);
	}


	public static Map<?, ?> objectToMap(@NonNull Object source) throws IOException {

		// Serialize the source object
		String json = objectToJson(source);

		// Deserialize the json
		return jsonToObject(json, Map.class);
	}

}
