package br.com.seuorg.artistas_api.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para operações relacionadas a Álbuns.
 *
 * Esta classe representa os dados retornados pela API
 * ao consultar, criar ou atualizar um álbum.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponseDTO {

    /**
     * Identificador único do álbum.
     */
    private Long id;

    /**
     * Nome do álbum.
     */
    private String nome;

    /**
     * Identificador do artista associado ao álbum.
     */
    private Long artistaId;

    /**
     * Nome do artista associado ao álbum.
     */
    private String artistaNome;

    /**
     * Data e hora de criação do álbum.
     * Formato ISO-8601: yyyy-MM-dd'T'HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização do álbum.
     * Formato ISO-8601: yyyy-MM-dd'T'HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
