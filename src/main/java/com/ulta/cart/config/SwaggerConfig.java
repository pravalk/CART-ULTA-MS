package com.ulta.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author BrijendraK
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@SuppressWarnings("deprecation")
	public static final ApiInfo DEFAULT_CUSTOM_INFO = new ApiInfo("Commerce Tool Product Microservice API Documents",
			"Product Microservice API Documents", "1.0.0", "urn:tos", "brijendrak@hcl.com", "Cloud Native",
			"http://www.pivotal.org/licenses/LICENSE-2.0");

	@Bean
	public Docket postsApi() {

		return new Docket(DocumentationType.SWAGGER_2).apiInfo(DEFAULT_CUSTOM_INFO);

	}

}
