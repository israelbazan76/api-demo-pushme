package com.bazan.demopushme.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    public EnvConfig() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue()));
    }
}
