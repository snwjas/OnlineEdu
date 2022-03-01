package xyz.refrain.onlineedu.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.refrain.onlineedu.config.properties.AliyunOssProperties;
import xyz.refrain.onlineedu.config.properties.AliyunProperties;
import xyz.refrain.onlineedu.config.properties.AliyunVodProperties;

/**
 * Aliyun Config
 *
 * @author Myles Yang
 */
@Configuration
public class AliyunConfig {

	@Autowired
	private AliyunProperties aliyunProperties;

	@Autowired
	private AliyunOssProperties aliyunOssProperties;

	@Autowired
	private AliyunVodProperties aliyunVodProperties;

	@Bean
	public OSS ossClient() {
		return new OSSClientBuilder().build(
				aliyunOssProperties.getEndpoint(),
				aliyunProperties.getAccessKeyId(),
				aliyunProperties.getAccessKeySecret()
		);
	}

	@Bean
	public DefaultAcsClient vodClient() {
		DefaultProfile vodProfile = DefaultProfile.getProfile(
				aliyunVodProperties.getRegionId(),
				aliyunProperties.getAccessKeyId(),
				aliyunProperties.getAccessKeySecret()
		);
		return new DefaultAcsClient(vodProfile);
	}


}
