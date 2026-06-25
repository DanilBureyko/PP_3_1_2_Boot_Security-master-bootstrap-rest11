package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/user.html").setViewName("user");
        registry.addViewController("/admin.html").setViewName("admin");
        registry.addViewController("/edit.html").setViewName("edit");
        registry.addViewController("/delete.html").setViewName("delete");
    }
}
