package eu.nimble.service.epcis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.annotations.Api;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import io.swagger.annotations.ApiOperation;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${nimble.platformHost}")
    private String platformHost;

    @Bean
    public Docket api() {

        platformHost = platformHost.replace("https://", "");
        platformHost = platformHost.replace("http://","");

        return new Docket(DocumentationType.SWAGGER_2)
                .host(platformHost)
                .select()
                //.apis(RequestHandlerSelectors.basePackage("org.oliot.epcis.service"))
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                //.apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
        
    }
    
 
    private ApiInfo metaData(){
        return new ApiInfoBuilder().title("NIMBLE Server EPCIS REST API")
                .description("REST API handling EPCIS services on the NIMBLE platform")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .build();
    }
    
}
