package kr.co.bigsnow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ezpmp")
public class BigsnowConfigProperties {

    private String basePackage;

    private String aesKey;

    private String oauthKey;

    private String issKey;

    private String algKey;
}
