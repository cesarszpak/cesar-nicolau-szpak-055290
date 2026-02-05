package br.com.seuorg.artistas_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade que representa um Álbum de um Artista.
 * Cada álbum pertence a um único artista e pode ter várias capas com exclusão em cascata.
 */
@Entity
@Table(name = "albuns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    /** Identificador único do álbum */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do álbum, obrigatório */
    @Column(nullable = false)
    private String nome;

    /** Relação Many-to-One com Artista */
    @ManyToOne
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    /** Relacionamento One-to-Many com CapaAlbum (exclusão em cascata) */
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapaAlbum> capas;

    /** Data de criação do registro, preenchida automaticamente */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data da última atualização do registro */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método chamado automaticamente antes de inserir o registro.
     * Define a data de criação como o momento atual.
     */
    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Método chamado automaticamente antes de atualizar o registro.
     * Define a data de atualização como o momento atual.
     */
    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
