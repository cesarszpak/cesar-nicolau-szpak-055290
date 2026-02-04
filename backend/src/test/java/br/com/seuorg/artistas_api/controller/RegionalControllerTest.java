package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.service.RegionalSyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste do controller administrativo de Regionais.
 * Verifica se o endpoint de sincronização POST /admin/regionais/sync
 * aciona o serviço de sincronização e retorna o relatório corretamente.
 */
@WebMvcTest(controllers = RegionalController.class)
class RegionalControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RegionalSyncService syncService;

    /**
     * Testa se o endpoint de sincronização funciona para usuários com role ADMIN.
     */
    @Test
    @DisplayName("POST /admin/regionais/sync aciona a sincronização e retorna relatório")
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void syncEndpoint() throws Exception {
        var report = new RegionalSyncService.SyncReport();
        report.inserted = 2;
        report.inactivated = 0;

        when(syncService.syncRegionais()).thenReturn(report);

        mvc.perform(post("/admin/regionais/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inserted").value(2));
    }
}
