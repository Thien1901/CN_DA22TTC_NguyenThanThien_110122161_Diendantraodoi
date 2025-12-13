package com.example.forum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString().replace("\\", "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + absolutePath + "/");
    }
}
