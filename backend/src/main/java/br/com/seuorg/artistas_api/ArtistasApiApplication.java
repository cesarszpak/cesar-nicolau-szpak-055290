package br.com.seuorg.artistas_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot.
 * Responsável por iniciar o contexto da aplicação e executar o servidor.
 */
@SpringBootApplication
public class ArtistasApiApplication {

    public static void main(String[] args) {
        // Inicia a aplicação Spring Boot
        SpringApplication.run(ArtistasApiApplication.class, args);
    }
}
