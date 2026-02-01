package br.com.seuorg.artistas_api.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de resposta para a capa do álbum.
 *
 * Esta classe é utilizada para transportar os dados da capa de um álbum
 * da aplicação para o cliente (ex: API REST), sem expor diretamente
 * a entidade do domínio.
 */
@Data
public class CapaAlbumResponseDTO {

    /** Identificador único da capa do álbum */
    private Long id;

    /** Identificador do álbum ao qual a capa pertence */
    private Long albumId;

    /** Chave utilizada para identificar o arquivo (ex: nome no storage ou S3) */
    private String chave;

    /** URL pública ou interna para acesso à capa do álbum */
    private String url;

    /** Nome original do arquivo enviado */
    private String nomeArquivo;

    /** Tipo de conteúdo do arquivo (MIME type), ex: image/png */
    private String contentType;

    /** Tamanho do arquivo em bytes */
    private Long tamanho;

    /** Data e hora em que a capa foi criada */
    private LocalDateTime createdAt;
}
