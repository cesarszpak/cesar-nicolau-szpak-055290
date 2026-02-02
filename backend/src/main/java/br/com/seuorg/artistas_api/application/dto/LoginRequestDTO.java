package br.com.seuorg.artistas_api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO responsável por representar os dados de login do usuário.
 * Utilizado no processo de autenticação.
 */
public class LoginRequestDTO {

    /**
     * E-mail do usuário.
     * Deve ser um e-mail válido e não pode ser vazio.
     */
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    /**
     * Senha do usuário.
     * Campo obrigatório para autenticação.
     */
    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    /**
     * Retorna o e-mail do usuário.
     *
     * @return e-mail informado no login
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o e-mail do usuário.
     *
     * @param email e-mail informado no login
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return senha informada no login
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha do usuário.
     *
     * @param senha senha informada no login
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
