package xyz.refrain.onlineedu.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Aliyun Properties
 *
 * @author Myles Yang
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {

	/**
	 * 阿里云 API 访问Id
	 */
	@NotEmpty
	private String accessKeyId;

	/**
	 * 阿里云 API 访问密钥
	 */
	@NotEmpty
	private String accessKeySecret;


}
