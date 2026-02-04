package br.com.seuorg.artistas_api.service;

import br.com.seuorg.artistas_api.domain.model.Regional;
import br.com.seuorg.artistas_api.domain.repository.RegionalRepository;
import br.com.seuorg.artistas_api.integration.regionais.ExternalRegionalDto;
import br.com.seuorg.artistas_api.integration.regionais.RegionaisClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testes de integração do serviço de sincronização de regionais.
 * Utiliza banco em memória com DataJpaTest e mock do cliente externo.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class RegionalSyncServiceTest {

    @Autowired
    RegionalRepository repository;

    @MockBean
    RegionaisClient client;

    /**
     * Deve inserir novas regionais quando não existem registros locais.
     */
    @Test
    void sync_insertsNew() {
        when(client.fetchAll()).thenReturn(List.of(
                new ExternalRegionalDto(9, "REGIONAL DE CUIABÁ"),
                new ExternalRegionalDto(31, "REGIONAL DE GUARANTÃ DO NORTE")
        ));

        RegionalSyncService service = new RegionalSyncService(repository, client);
        var report = service.syncRegionais();

        assertThat(report.getInserted()).isEqualTo(2);
        assertThat(repository.findByAtivoTrue()).hasSize(2);
    }

    /**
     * Deve inativar regionais locais que não existem mais no sistema externo.
     */
    @Test
    void sync_inactivatesMissing() {
        Regional r = Regional.builder().externalId(9).nome("REGIONAL DE CUIABÁ").ativo(true).build();
        repository.save(r);

        when(client.fetchAll()).thenReturn(List.of());

        RegionalSyncService service = new RegionalSyncService(repository, client);
        var report = service.syncRegionais();

        assertThat(report.getInactivated()).isEqualTo(1);
        assertThat(repository.findByAtivoTrue()).isEmpty();
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).isAtivo()).isFalse();
    }

    /**
     * Deve inativar a regional antiga e criar uma nova quando o nome for alterado.
     */
    @Test
    void sync_attributeChanged_inactivateAndCreateNew() {
        Regional r = Regional.builder().externalId(9).nome("OLD NAME").ativo(true).build();
        repository.save(r);

        when(client.fetchAll()).thenReturn(List.of(
                new ExternalRegionalDto(9, "REGIONAL DE CUIABÁ")
        ));

        RegionalSyncService service = new RegionalSyncService(repository, client);
        var report = service.syncRegionais();

        assertThat(report.getInserted()).isEqualTo(1);
        assertThat(report.getInactivated()).isEqualTo(1);

        var active = repository.findByAtivoTrue();
        assertThat(active).hasSize(1);
        assertThat(active.get(0).getNome()).isEqualTo("REGIONAL DE CUIABÁ");

        var all = repository.findAll();
        assertThat(all).hasSize(2);
    }
}
