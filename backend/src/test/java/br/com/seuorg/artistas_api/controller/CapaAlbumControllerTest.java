package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO;
import br.com.seuorg.artistas_api.application.service.CapaAlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração para o endpoint de upload de capas de álbuns.
 * Verifica se é possível enviar arquivos multipart e receber o DTO da capa criada.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Desabilita filtros de segurança para facilitar o teste
class CapaAlbumControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP para os endpoints

    @MockBean
    private CapaAlbumService capaService; // Mock do serviço para não depender de S3 ou banco real

    @Test
    void upload_shouldReturnListOfCreatedCovers() throws Exception {
        // Cria um arquivo multipart simulado
        MockMultipartFile file = new MockMultipartFile(
                "arquivos", // nome do campo esperado pelo endpoint
                "cap.jpg", // nome do arquivo
                "image/jpeg", // tipo de conteúdo
                "data".getBytes() // conteúdo do arquivo
        );

        // Cria um DTO simulado que será retornado pelo serviço
        CapaAlbumResponseDTO dto = new CapaAlbumResponseDTO();
        dto.setId(5L);
        dto.setAlbumId(1L);
        dto.setNomeArquivo("cap.jpg");
        dto.setChave("capas-album/albums/1/cap.jpg");
        dto.setUrl("http://localhost:9000/capas/capas-album/albums/1/cap.jpg");
        dto.setCreatedAt(LocalDateTime.now());

        // Configura o comportamento do mock para retornar o DTO quando o serviço for chamado
        when(capaService.criar(anyLong(), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(List.of(dto));

        // Executa a requisição POST multipart simulada e verifica a resposta
        mockMvc.perform(multipart("/api/capas")
                .file(file) // envia o arquivo
                .param("albumId", "1") // envia o parâmetro albumId
                .contentType(MediaType.MULTIPART_FORM_DATA)) // define o content type
                .andExpect(status().isOk()) // espera status 200 OK
                .andExpect(jsonPath("$[0].id").value(5)) // valida o id do DTO retornado
                .andExpect(jsonPath("$[0].albumId").value(1)) // valida o albumId
                .andExpect(jsonPath("$[0].nomeArquivo").value("cap.jpg")); // valida o nome do arquivo
    }
}
