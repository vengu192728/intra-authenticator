package com.intranet.authenticator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer(AuthProperties authProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                var cors = authProperties.getCors();
                var registration = registry.addMapping("/api/**")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600);

                if (!cors.getAllowedOriginPatterns().isEmpty()) {
                    registration.allowedOriginPatterns(
                            cors.getAllowedOriginPatterns().toArray(String[]::new));
                } else if (!cors.getAllowedOrigins().isEmpty()) {
                    registration.allowedOrigins(cors.getAllowedOrigins().toArray(String[]::new));
                } else {
                    registration.allowedOriginPatterns("*");
                }
            }
        };
    }
}
