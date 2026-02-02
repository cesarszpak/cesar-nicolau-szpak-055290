package br.com.seuorg.artistas_api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO responsável por representar os dados necessários
 * para a criação de um novo usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateDTO {

    /**
     * Nome do usuário.
     * Campo obrigatório e deve conter entre 2 e 255 caracteres.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String nome;

    /**
     * Email do usuário.
     * Campo obrigatório e deve ser um endereço de email válido.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    /**
     * Senha do usuário.
     * Campo obrigatório e deve conter entre 6 e 255 caracteres.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 255, message = "Senha deve ter entre 6 e 255 caracteres")
    private String senha;
}
