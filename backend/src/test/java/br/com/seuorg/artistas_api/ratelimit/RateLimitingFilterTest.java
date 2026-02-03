package br.com.seuorg.artistas_api.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração responsável por validar o funcionamento
 * do filtro de rate limit da aplicação.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RateLimitingFilterTest {

    // MockMvc utilizado para simular requisições HTTP
    @Autowired
    private MockMvc mvc;

    /**
     * Verifica que não existe mais um rate limit aplicado globalmente
     * às requisições da API (o limite agora é aplicado apenas ao envio
     * de notificações internamente).
     */
    @Test
    @WithMockUser(username = "testuser")
    void should_allow_more_than_10_requests_since_global_limit_removed() throws Exception {

        // Executa 11 requisições — todas devem ser 200 OK
        for (int i = 0; i < 11; i++) {
            mvc.perform(get("/api/albuns"))
               .andExpect(status().isOk());
        }
    }
}
