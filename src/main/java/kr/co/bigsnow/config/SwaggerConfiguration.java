package kr.co.bigsnow.config;


import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
    
	@Configuration
	@EnableSwagger2
	public class SwaggerConfig {

	    private ApiInfo apiInfo() {

	        return new ApiInfoBuilder()
	                .title("Snow api info")
	                .description("Devcrews API")
	                .build();
	    }

	    @Bean
	    public Docket commonApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .groupName("DevCrews")
	                .apiInfo(this.apiInfo())
	                .select()
	                // .apis(RequestHandlerSelectors.basePackage("com.contest.calendar.controller"))
	                .apis(RequestHandlerSelectors.basePackage("kr.co.bigsnow.api"))
	                .paths(PathSelectors.ant("/api/**"))
	                .build();
	    }

	}
	
	 /*
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          //.apis(RequestHandlerSelectors.any())
          .apis(RequestHandlerSelectors.basePackage("kr.co.bigsnow.api"))
          // .paths(PathSelectors.any())
          .paths(PathSelectors.ant("/api/**"))
          .build();                                           
    }
    */
}