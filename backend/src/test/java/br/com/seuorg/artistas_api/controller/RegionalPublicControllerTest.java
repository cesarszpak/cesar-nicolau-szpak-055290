package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.integration.regionais.ExternalRegionalDto;
import br.com.seuorg.artistas_api.service.RegionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes do controller público de Regionais.
 * Verifica endpoints de listagem, busca por ID e sincronização com API externa.
 */
@WebMvcTest(controllers = RegionalPublicController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class RegionalPublicControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RegionalService service;

    /**
     * Testa se GET /api/regionais retorna uma página com as regionais ativas.
     */
    @Test
    @DisplayName("GET /api/regionais retorna resultados paginados")
    void listRegionais() throws Exception {
        var r1 = new br.com.seuorg.artistas_api.domain.model.Regional();
        r1.setId(1L);
        r1.setExternalId(9);
        r1.setNome("REGIONAL DE CUIABÁ");
        r1.setAtivo(true);
        r1.setCreatedAt(LocalDateTime.now());

        var r2 = new br.com.seuorg.artistas_api.domain.model.Regional();
        r2.setId(2L);
        r2.setExternalId(31);
        r2.setNome("REGIONAL DE GUARANTÃ DO NORTE");
        r2.setAtivo(true);
        r2.setCreatedAt(LocalDateTime.now());

        when(service.listAtivas(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(r1, r2), PageRequest.of(0, 10), 2));

        mvc.perform(get("/api/regionais").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nome").value("REGIONAL DE CUIABÁ"));
    }

    /**
     * Testa se GET /api/regionais/{id} retorna uma única regional corretamente.
     */
    @Test
    @DisplayName("GET /api/regionais/{id} retorna uma regional")
    void getRegional() throws Exception {
        var r = new br.com.seuorg.artistas_api.domain.model.Regional();
        r.setId(1L);
        r.setExternalId(9);
        r.setNome("REGIONAL DE CUIABÁ");
        r.setAtivo(true);
        r.setCreatedAt(LocalDateTime.now());

        when(service.findById(1L)).thenReturn(Optional.of(r));

        mvc.perform(get("/api/regionais/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("REGIONAL DE CUIABÁ"));
    }

    /**
     * Testa se GET /api/regionais/external executa a sincronização
     * com a API externa e retorna relatório em português.
     */
    @Test
    @DisplayName("GET /api/regionais/external faz sincronização e retorna relatório em português")
    void getExternal() throws Exception {
        var lista = List.of(new ExternalRegionalDto(9, "REGIONAL DE CUIABÁ"));
        when(service.fetchExternas()).thenReturn(lista);

        // Relatório esperado da sincronização
        var report = new br.com.seuorg.artistas_api.service.RegionalSyncService.SyncReport();
        report.inserted = 1;
        report.inactivated = 0;
        when(service.syncExternas()).thenReturn(report);

        mvc.perform(get("/api/regionais/external").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Sincronização concluída: 1 inseridas, 0 inativadas"))
                .andExpect(jsonPath("$.externas[0].nome").value("REGIONAL DE CUIABÁ"));
    }
}
