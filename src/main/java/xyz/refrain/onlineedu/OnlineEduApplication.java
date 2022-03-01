package xyz.refrain.onlineedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application Entrance
 *
 * @author Myles Yang
 * @date 2021-05-01
 */
@EnableScheduling
@ConfigurationPropertiesScan("xyz.refrain.onlineedu.config.properties")
@SpringBootApplication
public class OnlineEduApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineEduApplication.class, args);
	}

}
