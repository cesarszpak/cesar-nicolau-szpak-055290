package br.com.seuorg.artistas_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Usuário do sistema.
 * Contém informações básicas de cadastro e autenticação.
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /** Identificador único do usuário */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome completo do usuário, obrigatório */
    @Column(nullable = false)
    private String nome;

    /** Email do usuário, único e obrigatório, usado para login */
    @Column(nullable = false, unique = true)
    private String email;

    /** Senha do usuário, armazenada criptografada */
    @Column(nullable = false)
    private String senha;

    /** Data de criação do registro, preenchida automaticamente */
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
