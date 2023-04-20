package com.example.springboot.data.jpa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
}
