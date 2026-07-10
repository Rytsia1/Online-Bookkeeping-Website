package com.DTMK.Online.Bookkeeping.Website.Project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Berlaku untuk semua endpoint (/api/user, /api/bill, dll)
                .allowedOrigins("http://localhost:5173", "http://localhost:3000") // Mengizinkan URL Frontend Vue/Vite
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Mengizinkan metode HTTP ini
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}