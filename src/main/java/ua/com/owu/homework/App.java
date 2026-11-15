package ua.com.owu.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class App implements WebMvcConfigurer {
    @Value("${app.upload-dir:uploads}")
    private String uploadDirectory;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path directory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(directory.toUri().toString());
    }
}
