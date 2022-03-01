package xyz.refrain.onlineedu.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 请求、响应 日期格式全局格式化
 */
@Configuration
public class LocalDateTimeSerializerConfig {

	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";


	/**
	 * 自定义 LocalDateTime、LocalDate、LocalTime 的序列化 与 反序列化
	 * 配置 MappingJackson2HttpMessageConverter{@link WebMvcConfig#customJackson2HttpMessageConverter()}
	 * 重写 WebMVC 的 configureMessageConverters{@link WebMvcConfig#configureMessageConverters(List)}
	 */
	@Bean
	@Primary
	public ObjectMapper objectMapper() {

		ObjectMapper om = new ObjectMapper();
		om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

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
}
