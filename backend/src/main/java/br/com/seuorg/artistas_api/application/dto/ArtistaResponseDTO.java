package br.com.seuorg.artistas_api.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO responsável por representar os dados de resposta
 * de um artista retornados pela API.
 */
@Data // Gera getters, setters, equals, hashCode e toString
@NoArgsConstructor // Gera construtor sem argumentos
@AllArgsConstructor // Gera construtor com todos os atributos
public class ArtistaResponseDTO {

    /**
     * Identificador único do artista.
     */
    private Long id;

    /**
     * Nome do artista.
     */
    private String nome;

    /**
     * Quantidade de álbuns associados ao artista.
     */
    private Long albumCount;

    /**
     * Data e hora de criação do registro.
     * Formato padrão ISO-8601.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização do registro.
     * Formato padrão ISO-8601.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
