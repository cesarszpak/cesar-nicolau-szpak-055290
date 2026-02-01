package br.com.seuorg.artistas_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa a capa de um álbum.
 *
 * Esta classe mapeia a tabela "capas_album" no banco de dados e
 * armazena as informações relacionadas aos arquivos de capa,
 * incluindo metadados e vínculo com o álbum.
 */
@Entity
@Table(name = "capas_album")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapaAlbum {

    /** Identificador único da capa do álbum */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Álbum ao qual a capa pertence */
    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    /** Chave do arquivo no armazenamento (ex: S3 ou MinIO) */
    @Column(nullable = false, length = 1024)
    private String chave;

    /** Nome original do arquivo enviado */
    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    /** Tipo de conteúdo do arquivo (MIME type) */
    @Column(name = "content_type")
    private String contentType;

    /** Tamanho do arquivo em bytes */
    private Long tamanho;

    /** Data e hora de criação do registro */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data e hora da última atualização do registro */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Define automaticamente a data de criação antes
     * da persistência do registro.
     */
    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Atualiza automaticamente a data de modificação
     * antes da atualização do registro.
     */
    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
