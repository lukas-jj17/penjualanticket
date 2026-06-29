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

    public static final String UPLOAD_DIR = System.getProperty("user.home") + java.io.File.separator + "penjualanticket-uploads";

    public WebConfig() {
        try {
            java.io.File newDir = new java.io.File(UPLOAD_DIR);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            
            java.io.File oldDir = new java.io.File("uploads");
            if (!oldDir.exists() || !oldDir.isDirectory()) {
                oldDir = new java.io.File("penjualanticket/uploads");
            }
            
            if (oldDir.exists() && oldDir.isDirectory()) {
                java.io.File[] files = oldDir.listFiles();
                if (files != null) {
                    for (java.io.File file : files) {
                        if (file.isFile()) {
                            java.io.File destFile = new java.io.File(newDir, file.getName());
                            if (!destFile.exists()) {
                                java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR + "/");
    }
}
