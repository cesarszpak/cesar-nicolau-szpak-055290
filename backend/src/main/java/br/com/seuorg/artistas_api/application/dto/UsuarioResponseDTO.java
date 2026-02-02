package br.com.seuorg.artistas_api.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO responsável por representar os dados de resposta
 * de um usuário retornados pela API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    /**
     * Identificador único do usuário.
     */
    private Long id;

    /**
     * Nome do usuário.
     */
    private String nome;

    /**
     * Email do usuário.
     */
    private String email;

    /**
     * Data e hora de criação do usuário.
     * Formatada no padrão ISO-8601.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
