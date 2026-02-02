package br.com.seuorg.artistas_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Artista.
 * Um artista pode ter vários álbuns (relacionamento unidirecional na entidade Album).
 */
@Entity
@Table(name = "artistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artista {

    /** Identificador único do artista */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do artista, campo obrigatório */
    @Column(nullable = false)
    private String nome;

    /** Data de criação do registro, preenchida automaticamente */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data da última atualização do registro */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método chamado automaticamente antes de inserir o registro no banco.
     * Define a data de criação como o momento atual.
     */
    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Método chamado automaticamente antes de atualizar o registro no banco.
     * Define a data de atualização como o momento atual.
     */
    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
