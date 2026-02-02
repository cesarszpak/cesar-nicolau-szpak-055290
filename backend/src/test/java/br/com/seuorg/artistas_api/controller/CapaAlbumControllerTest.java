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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // desabilita filtros de segurança para facilitar o teste
class CapaAlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CapaAlbumService capaService;

    @Test
    void upload_shouldReturnListOfCreatedCovers() throws Exception {
        MockMultipartFile file = new MockMultipartFile("arquivos", "cap.jpg", "image/jpeg", "data".getBytes());

        CapaAlbumResponseDTO dto = new CapaAlbumResponseDTO();
        dto.setId(5L);
        dto.setAlbumId(1L);
        dto.setNomeArquivo("cap.jpg");
        dto.setChave("capas-album/albums/1/cap.jpg");
        dto.setUrl("http://localhost:9000/capas/capas-album/albums/1/cap.jpg");
        dto.setCreatedAt(LocalDateTime.now());

        when(capaService.criar(anyLong(), org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of(dto));

        mockMvc.perform(multipart("/api/capas")
                .file(file)
                .param("albumId", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].albumId").value(1))
                .andExpect(jsonPath("$[0].nomeArquivo").value("cap.jpg"));
    }
}
