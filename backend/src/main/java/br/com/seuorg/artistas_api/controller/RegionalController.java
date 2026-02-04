package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.service.RegionalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por endpoints administrativos relacionados às regionais.
 * Disponibiliza a operação de sincronização das regionais com a fonte de dados externa.
 */
@RestController
@RequestMapping("/admin/regionais")
@RequiredArgsConstructor
public class RegionalController {

    private final RegionalSyncService syncService;

    /**
     * Endpoint para executar a sincronização das regionais.
     * Retorna um relatório com o resultado do processo de sincronização.
     */
    @PostMapping("/sync")
    public ResponseEntity<?> sync() {
        RegionalSyncService.SyncReport report = syncService.syncRegionais();
        return ResponseEntity.ok(report);
    }
}
