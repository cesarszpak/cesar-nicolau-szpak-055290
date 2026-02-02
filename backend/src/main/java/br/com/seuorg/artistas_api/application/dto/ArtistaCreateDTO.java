package br.com.seuorg.artistas_api.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO responsável por representar os dados necessários
 * para a criação de um artista.
 */
@Data // Gera getters, setters, equals, hashCode e toString
@NoArgsConstructor // Gera construtor sem argumentos
@AllArgsConstructor // Gera construtor com todos os atributos
public class ArtistaCreateDTO {

    /**
     * Nome do artista.
     * Campo obrigatório e com tamanho entre 2 e 255 caracteres.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String nome;
}
