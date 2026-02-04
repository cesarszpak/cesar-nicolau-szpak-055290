package br.com.seuorg.artistas_api.integration.regionais;

import java.util.List;

/**
 * Interface que define o contrato para clientes responsáveis
 * por buscar regionais em sistemas externos.
 */
public interface RegionaisClient {

    /**
     * Retorna todas as regionais obtidas da integração externa.
     */
    List<ExternalRegionalDto> fetchAll();
}
