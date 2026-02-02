package br.com.seuorg.artistas_api.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para criação e atualização de álbuns.
 *
 * Esta classe representa os dados mínimos necessários
 * para cadastrar ou editar um álbum no sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreateDTO {

    /**
     * Nome do álbum.
     * - Obrigatório
     * - Deve possuir entre 2 e 255 caracteres
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String nome;

    /**
     * Identificador do artista ao qual o álbum pertence.
     * - Obrigatório
     */
    @NotNull(message = "Artista ID é obrigatório")
    private Long artistaId;

}
