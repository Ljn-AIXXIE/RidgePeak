package org.ridgepeak.backend;

import org.ridgepeak.backend.handler.TokenAuthHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {
    private final TokenAuthHandler tokenAuthHandler;

    public WebConfig(TokenAuthHandler tokenAuthHandler) {
        this.tokenAuthHandler = tokenAuthHandler;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenAuthHandler)
                .addPathPatterns("/api/auth/logout")
				.addPathPatterns("/api/profile/me", "/api/profile/me/**");;
    }

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:uploads/");
	}
}

@SpringBootApplication
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
