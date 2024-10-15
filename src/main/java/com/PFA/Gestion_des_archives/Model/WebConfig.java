package com.PFA.Gestion_des_archives.Model;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permet toutes les requêtes
                .allowedOrigins("http://192.168.21.188:3002") // Changez ceci si votre React est sur un autre port
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Spécifiez les méthodes HTTP autorisées
                .allowedHeaders("*") // Autoriser tous les en-têtes
                .allowCredentials(true); // Permettre les cookies
    }
}
