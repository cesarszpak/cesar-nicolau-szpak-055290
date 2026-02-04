package br.com.seuorg.artistas_api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma Regional no sistema.
 * Mapeia a tabela "regional" no banco de dados e armazena
 * informações básicas como nome, status e identificador externo.
 */
@Entity
@Table(name = "regional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Regional {

    /**
     * Identificador interno da regional (chave primária).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador da regional no sistema externo.
     */
    @Column(name = "external_id")
    private Integer externalId;

    /**
     * Nome da regional.
     */
    @Column(name = "nome", length = 200, nullable = false)
    private String nome;

    /**
     * Indica se a regional está ativa.
     */
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private boolean ativo = true;

    /**
     * Data e hora de criação do registro.
     */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Define automaticamente a data de criação antes de persistir,
     * caso não tenha sido informada.
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
