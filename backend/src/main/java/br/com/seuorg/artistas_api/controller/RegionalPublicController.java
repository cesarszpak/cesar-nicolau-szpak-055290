package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.domain.model.Regional;
import br.com.seuorg.artistas_api.integration.regionais.ExternalRegionalDto;
import br.com.seuorg.artistas_api.service.RegionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller público para consulta de regionais.
 * Permite listar, buscar por ID e executar sincronização com a API externa.
 */
@RestController
@RequestMapping("/api/regionais")
@RequiredArgsConstructor
public class RegionalPublicController {

    private final RegionalService service;

    /**
     * DTO utilizado para expor dados de Regional via API.
     */
    record RegionalDto(Long id, Integer externalId, String nome, boolean ativo, String createdAt) {
    }

    /**
     * Converte a entidade Regional em DTO.
     */
    private RegionalDto toDto(Regional r) {
        return new RegionalDto(
                r.getId(),
                r.getExternalId(),
                r.getNome(),
                r.isAtivo(),
                r.getCreatedAt().toString()
        );
    }

    /**
     * Lista regionais ativas com paginação.
     */
    @GetMapping
    public ResponseEntity<Page<RegionalDto>> list(Pageable pageable) {
        Page<Regional> page = service.listAtivas(pageable);
        Page<RegionalDto> dtoPage = page.map(this::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Busca uma regional pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionalDto> get(@PathVariable Long id) {
        return service.findById(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca regionais da API externa e executa a sincronização com o banco.
     */
    @GetMapping("/external")
    public ResponseEntity<ExternalSyncResponse> external() {

        // Recupera lista externa
        List<ExternalRegionalDto> list = service.fetchExternas();

        try {
            // Executa a sincronização
            var report = service.syncExternas();

            String mensagem = String.format(
                    "Sincronização concluída: %d inseridas, %d inativadas",
                    report.inserted, report.inactivated
            );

            ExternalSyncResponse resp = new ExternalSyncResponse(
                    mensagem,
                    report.inserted,
                    report.inactivated,
                    list
            );

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            // Retorna erro em português
            ExternalSyncResponse errorResp = new ExternalSyncResponse(
                    "Falha na sincronização: " + e.getMessage(),
                    0,
                    0,
                    list
            );

            return ResponseEntity.status(500).body(errorResp);
        }
    }

    /**
     * Estrutura de resposta do endpoint de sincronização externa.
     */
    record ExternalSyncResponse(
            String mensagem,
            int inseridas,
            int inativadas,
            List<ExternalRegionalDto> externas
    ) {
    }
}
