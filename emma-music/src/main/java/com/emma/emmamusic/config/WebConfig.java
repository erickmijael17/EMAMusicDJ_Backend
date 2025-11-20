//package com.emma.emmamusic.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.List;
//
//
//@Configuration
//public class WebConfig {
//
//    @Value("${cors.allowed-origins}")
//    private List<String> allowedOrigins;
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // Aplica la configuración a todas las rutas de la API.
//                        .allowedOrigins(allowedOrigins.toArray(new String[0])) // Orígenes permitidos (ej: "http://localhost:4200").
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos.
//                        .allowedHeaders("*") // Permite cualquier cabecera en la solicitud.
//                        .allowCredentials(true) // Permite el envío de cookies y cabeceras de autenticación.
//                        .maxAge(3600); // Tiempo en segundos que el navegador puede cachear la respuesta pre-flight.
//            }
//        };
//    }
//}