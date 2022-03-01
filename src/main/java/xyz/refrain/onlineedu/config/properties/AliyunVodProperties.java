package xyz.refrain.onlineedu.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * Aliyun Vod Properties
 *
 * @author Myles Yang
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "aliyun.vod")
public class AliyunVodProperties {

	/**
	 * 点播服务接入点
	 */
	@NotBlank
	private String regionId;

}
