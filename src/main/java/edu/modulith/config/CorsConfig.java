package edu.modulith.config;

// Trong lớp cấu hình Spring Security hoặc một lớp @Configuration riêng biệt
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Áp dụng cho TẤT CẢ các API endpoints
                        .allowedOrigins("http://localhost:4200") // CHỈ cho phép frontend của bạn
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các method được phép
                        .allowedHeaders("*") // Cho phép tất cả headers
                        .allowCredentials(true); // Quan trọng nếu bạn dùng Cookie/Session/Token
            }
        };
    }
}