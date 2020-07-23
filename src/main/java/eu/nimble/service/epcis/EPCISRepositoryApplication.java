package eu.nimble.service.epcis;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
* Created by Quan Deng, 2019
*/

//import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCaching
@Configuration
//@EnableCircuitBreaker
//@EnableEurekaClient
//@EnableFeignClients
@RestController
//@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
//@ServletComponentScan
@ComponentScan(basePackages = "eu.nimble.service.epcis, org.oliot.epcis")
//@ComponentScan(basePackages = "eu.nimble.service.epcis")
public class EPCISRepositoryApplication implements CommandLineRunner {

    @Override
    public void run(String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder.build();

    }

    public static void main(String[] args) throws Exception {
        new SpringApplication(EPCISRepositoryApplication.class).run(args);
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}