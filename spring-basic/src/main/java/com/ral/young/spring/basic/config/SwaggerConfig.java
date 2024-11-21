package com.ral.young.spring.basic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author renyunhui
 * @description 这是一个SwaggerConfig类
 * @date 2024-11-21 10-05-01
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("basic")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ral.young.spring.basic"))
                .paths(PathSelectors.any())
                .build();
    }
}
