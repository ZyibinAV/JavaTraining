package com.homeapp.javatraining.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String UPLOAD_URL_PATTERN = "/uploads/avatars/";

    private final String uploadDir;

    public WebConfig(@Value("${app.avatar.upload-dir:uploads/avatars}") String uploadDirConfig) {
        java.nio.file.Path configured = Paths.get(uploadDirConfig);
        this.uploadDir = (configured.isAbsolute() ? configured : Paths.get(System.getProperty("user.dir")).resolve(uploadDirConfig).normalize()).toString();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(UPLOAD_URL_PATTERN + "**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
