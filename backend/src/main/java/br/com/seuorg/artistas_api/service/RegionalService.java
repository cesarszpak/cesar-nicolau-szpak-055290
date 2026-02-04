package br.com.seuorg.artistas_api.service;

import br.com.seuorg.artistas_api.domain.model.Regional;
import br.com.seuorg.artistas_api.domain.repository.RegionalRepository;
import br.com.seuorg.artistas_api.integration.regionais.ExternalRegionalDto;
import br.com.seuorg.artistas_api.integration.regionais.RegionaisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável por operações de Regional.
 * Fornece métodos para listar, buscar, buscar externas e sincronizar regionais.
 */
@Service
@RequiredArgsConstructor
public class RegionalService {

    private final RegionalRepository repository;
    private final RegionaisClient client;
    private final RegionalSyncService syncService; // Serviço de sincronização com API externa

    /**
     * Retorna uma página de regionais ativas.
     */
    public Page<Regional> listAtivas(Pageable pageable) {
        return repository.findByAtivoTrue(pageable);
    }

    /**
     * Busca uma regional pelo ID.
     */
    public Optional<Regional> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Recupera a lista de regionais do sistema externo.
     */
    public List<ExternalRegionalDto> fetchExternas() {
        return client.fetchAll();
    }

    /**
     * Executa a sincronização das regionais com a API externa
     * e retorna o relatório com o número de inserções e inativações.
     */
    public RegionalSyncService.SyncReport syncExternas() {
        return syncService.syncRegionais();
    }
}
