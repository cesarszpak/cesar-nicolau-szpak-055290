package br.com.seuorg.artistas_api.service;

import br.com.seuorg.artistas_api.domain.model.Regional;
import br.com.seuorg.artistas_api.domain.repository.RegionalRepository;
import br.com.seuorg.artistas_api.integration.regionais.ExternalRegionalDto;
import br.com.seuorg.artistas_api.integration.regionais.RegionaisClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável por sincronizar as regionais locais
 * com as regionais obtidas de um sistema externo.
 */
@Service
@RequiredArgsConstructor
public class RegionalSyncService {

    private final RegionalRepository repository;
    private final RegionaisClient client;

    /**
     * Relatório retornado após a sincronização.
     * Informa quantos registros foram inseridos e inativados.
     */
    @Getter
    public static class SyncReport {
        public int inserted;
        public int inactivated;
    }

    /**
     * Executa a sincronização das regionais.
     *
     * Regras:
     * 1) Regionais novas → são inseridas.
     * 2) Regionais com nome alterado → antigas são inativadas e novas são criadas.
     * 3) Regionais locais que não existem mais no externo → são inativadas.
     */
    @Transactional
    public SyncReport syncRegionais() {

        // Busca todas as regionais do sistema externo
        List<ExternalRegionalDto> external = client.fetchAll();

        // Mapeia regionais externas por ID para otimizar busca O(n+m)
        Map<Integer, ExternalRegionalDto> externalById = external.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(ExternalRegionalDto::getId, e -> e));

        // Busca regionais locais ativas
        List<Regional> activeLocals = repository.findByAtivoTrue();

        // Agrupa regionais locais ativas por externalId
        Map<Integer, List<Regional>> localsByExternal = activeLocals.stream()
                .filter(r -> r.getExternalId() != null)
                .collect(Collectors.groupingBy(Regional::getExternalId));

        SyncReport report = new SyncReport();

        // 1) Processa cada regional externa
        for (ExternalRegionalDto ext : externalById.values()) {
            Integer extId = ext.getId();
            List<Regional> locals = localsByExternal.getOrDefault(extId, List.of());

            // Verifica se já existe regional com mesmo nome
            boolean foundSameName = locals.stream()
                    .anyMatch(l -> l.getNome().equals(ext.getNome()));

            if (foundSameName) {
                // Nenhuma ação necessária
                continue;
            }

            if (locals.isEmpty()) {
                // Nova regional → inserir
                Regional created = Regional.builder()
                        .externalId(extId)
                        .nome(ext.getNome())
                        .ativo(true)
                        .createdAt(LocalDateTime.now())
                        .build();

                repository.save(created);
                report.inserted++;

            } else {
                // Nome alterado → inativa antigas e cria nova
                for (Regional loc : locals) {
                    loc.setAtivo(false);
                    repository.save(loc);
                }

                Regional created = Regional.builder()
                        .externalId(extId)
                        .nome(ext.getNome())
                        .ativo(true)
                        .createdAt(LocalDateTime.now())
                        .build();

                repository.save(created);
                report.inserted++;
                report.inactivated += locals.size();
            }
        }

        // 2) Regionais locais ativas que não existem mais no externo → inativar
        for (Regional local : activeLocals) {
            Integer extId = local.getExternalId();
            if (extId == null) continue; // ignora registros sem vínculo externo

            if (!externalById.containsKey(extId)) {
                local.setAtivo(false);
                repository.save(local);
                report.inactivated++;
            }
        }

        return report;
    }
}
