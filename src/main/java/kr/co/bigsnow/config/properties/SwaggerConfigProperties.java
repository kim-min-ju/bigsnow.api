package kr.co.bigsnow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "snow.swagger")
@EnableTransactionManagement
public class SwaggerConfigProperties {
	private boolean enabled = false;

	private String basePackage;
}
