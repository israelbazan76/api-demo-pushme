package com.bazan.demopushme.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    /*
    public EnvConfig() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue()));
    }
    */
    @PostConstruct
    public void loadEnv() {
        // Solo cargar .env en desarrollo local
        // Render inyecta la variable RENDER automáticamente
        if (System.getenv("RENDER") == null) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .ignoreIfMissing() // No lanzar excepción si no encuentra el archivo
                        .load();

                // Opcional: Poner las variables del .env en el System Environment
                // Esto asegura que @Value("${MI_VAR}") funcione tanto con .env como con variables del SO
                dotenv.entries().forEach(entry -> {
                    String key = entry.getKey();
                    if (System.getenv(key) == null) {
                        System.setProperty(key, entry.getValue());
                    }
                });
                
            } catch (Exception e) {
                System.out.println("Info: No .env file found. Using system environment variables.");
            }
        }
    }     
}
