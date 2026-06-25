package com.pbo2.penjualanticket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Memetakan URL /uploads/** ke folder runtime ./uploads di luar classpath,
 * sehingga gambar poster event yang di-upload bisa langsung tampil tanpa rebuild.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String UPLOAD_DIR = "uploads";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR + "/");
    }
}
