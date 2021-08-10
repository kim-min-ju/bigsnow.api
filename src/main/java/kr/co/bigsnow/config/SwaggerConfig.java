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
public class SwaggerConfig {
    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.bigsnow.api.controlle"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false); // 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
    }

    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder().title("Spring API Documentation")
                .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다")
                //.license("happydaddy").licenseUrl("http://daddyprogrammer.org").version("1")
                .build();
    }
}


/*





@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("Big Snow")
                .description("API")
                .build();
    }

    @Bean
    public Docket commonApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("example")
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors
                .basePackage("kr.co.bigsnow.api.controller"))
                //.paths(PathSelectors.ant("/**"))
                .paths(PathSelectors.any())
                .build();
    }

}

*/






/*

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
	*/
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
 