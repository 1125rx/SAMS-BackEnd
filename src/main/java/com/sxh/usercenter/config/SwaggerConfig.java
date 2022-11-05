package com.sxh.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @program: usercenter
 * @description: 自定义Swagger接口文档的配置
 * @author: SXH
 * @create: 2022-11-05 15:07
 **/
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sxh.usercenter.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Song's AppCenter")
                .description("Song's API documents of userCenter")
                .termsOfServiceUrl("https://github.com/1125rx")
                .contact(new Contact("SXH","https://github.com/1125rx","1770986733@qq.com"))
                .version("1.0")
                .build();
    }

}
