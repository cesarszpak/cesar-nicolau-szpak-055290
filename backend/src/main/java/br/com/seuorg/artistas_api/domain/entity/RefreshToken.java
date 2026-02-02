package br.com.seuorg.artistas_api.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um Refresh Token.
 * Usado para renovar JWTs sem que o usuário precise logar novamente.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshToken {

    /** Identificador único do token */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Valor do token, único e obrigatório */
    @Column(nullable = false, unique = true)
    private String token;

    /** Usuário associado ao token */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Data e hora de expiração do token */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Data de criação do token, preenchida automaticamente */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Método chamado automaticamente antes de persistir o registro no banco.
     * Define a data de criação como o momento atual.
     */
    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
