package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO;
import br.com.seuorg.artistas_api.application.service.CapaAlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração para o endpoint público de conteúdo da capa do álbum.
 * Verifica se a rota /api/capas/{id}/conteudo está acessível sem autenticação.
 */
@SpringBootTest
@AutoConfigureMockMvc // Mantém filtros de segurança ativos para testar autenticação/autorização
class CapaAlbumContentPublicTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP para os endpoints

    @MockBean
    private CapaAlbumService capaService; // Mock do serviço, para não depender do banco ou S3

    @Test
    void conteudo_shouldBeAccessibleWithoutAuth() throws Exception {
        // Cria um DTO de resposta simulado
        CapaAlbumResponseDTO dto = new CapaAlbumResponseDTO();
        dto.setId(13L);
        dto.setChave("capas/capas-album/albums/37/curso-default.jpg");
        dto.setContentType("image/jpeg");
        dto.setCreatedAt(LocalDateTime.now());

        // Configura o comportamento do mock
        when(capaService.findById(13L)).thenReturn(dto);
        when(capaService.downloadContent(dto.getChave())).thenReturn(new byte[] {1,2,3});

        // Executa a requisição GET simulada e verifica se retorna 200 OK
        mockMvc.perform(get("/api/capas/13/conteudo")
                .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk());
    }
}
