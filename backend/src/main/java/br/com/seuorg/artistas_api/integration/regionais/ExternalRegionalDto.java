package br.com.seuorg.artistas_api.integration.regionais;

import lombok.*;

/**
 * DTO utilizado para representar uma Regional proveniente
 * de um sistema externo durante o processo de integração.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalRegionalDto {

    /**
     * Identificador da regional no sistema externo.
     */
    private Integer id;

    /**
     * Nome da regional retornado pelo sistema externo.
     */
    private String nome;
}
