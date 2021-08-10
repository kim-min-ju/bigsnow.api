package kr.co.bigsnow.config;


import kr.co.bigsnow.config.properties.BigsnowConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ComponentScan({ "kr.co.bigsnow.core" })
@EnableAsync
@EnableConfigurationProperties(BigsnowConfigProperties.class)
public class BigsnowCoreConfig {

    @PostConstruct
    public void init() {
        log.info(">>> BigsnowCoreConfig.PostConstruct");
    }
}
