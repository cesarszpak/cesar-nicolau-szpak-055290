package br.com.seuorg.artistas_api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para padronizar a resposta de erros da API.
 * Contém informações sobre o erro ocorrido, como status HTTP, mensagem e caminho da requisição.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    
    /** Data e hora em que o erro ocorreu */
    private LocalDateTime timestamp;

    /** Código de status HTTP do erro */
    private int status;

    /** Tipo de erro (ex: "Not Found", "Bad Request") */
    private String error;

    /** Mensagem detalhada do erro */
    private String message;

    /** Caminho da requisição que gerou o erro */
    private String path;
}
