package kr.co.bigsnow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import lombok.Data;

@Data

@ConfigurationProperties(prefix = "ezpmp.build")
public class EzpmpBuildProperties {
    private String version;

    private String timestamp;
}
