package com.example.springboot.data.jpa.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
//    Logger log = LoggerFactory.getLogger(getClass());
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        WebMvcConfigurer.super.addResourceHandlers(registry);
//        String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
//        log.info("resource path: " + resourcePath);
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations(resourcePath);
//    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error_403").setViewName("error_403");
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
