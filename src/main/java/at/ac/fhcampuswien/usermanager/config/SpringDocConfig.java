package at.ac.fhcampuswien.usermanager.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SpringDocConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("ASD_User_Manager")
                .pathsToMatch("/user/**")
                .build();
    }
}