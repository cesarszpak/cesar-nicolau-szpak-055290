package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.domain.entity.Album;
import br.com.seuorg.artistas_api.domain.entity.CapaAlbum;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.CapaAlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço de capas de álbuns (CapaAlbumService).
 *
 * Valida o comportamento das regras de negócio relacionadas
 * ao upload, persistência e exclusão de capas de álbuns.
 */
class CapaAlbumServiceTest {

    /** Mock do repositório de capas de álbuns */
    @Mock
    private CapaAlbumRepository capaRepo;

    /** Mock do repositório de álbuns */
    @Mock
    private AlbumRepository albumRepo;

    /** Mock do serviço de armazenamento S3 */
    @Mock
    private br.com.seuorg.artistas_api.storage.S3StorageService s3;

    /** Serviço a ser testado, com dependências mockadas */
    @InjectMocks
    private CapaAlbumService service;

    /**
     * Inicializa os mocks antes da execução de cada teste.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Ensure Mockito injected mocks into the service. Some environments require an explicit set.
        org.springframework.test.util.ReflectionTestUtils.setField(service, "s3", s3);
        // In unit tests, @Value fields are not injected. Set them manually.
        org.springframework.test.util.ReflectionTestUtils.setField(service, "publicBaseUrl", "http://localhost:9000");
        org.springframework.test.util.ReflectionTestUtils.setField(service, "bucket", "capas");
        // Ensure upload limit is reasonable for tests (defaults aren't injected in unit tests)
        org.springframework.test.util.ReflectionTestUtils.setField(service, "maxFilesPerUpload", 10);
    }

    /**
     * Testa a criação de capas de álbum.
     *
     * Deve realizar o upload do arquivo no S3
     * e salvar os dados da capa no banco de dados.
     */
    @Test
    void criar_deveFazerUploadESalvar() throws IOException {
        Album a = new Album();
        a.setId(1L);

        // Simula a existência do álbum
        when(albumRepo.findById(1L)).thenReturn(Optional.of(a));

        // Cria um arquivo multipart simulado
        MockMultipartFile file = new MockMultipartFile(
                "arquivos",
                "cap.jpg",
                "image/jpeg",
                "data".getBytes()
        );

        CapaAlbum saved = new CapaAlbum();
        saved.setId(5L);
        saved.setAlbum(a); // garante que o álbum está associado na entidade retornada pelo repositório
        // fornece chave e nome do arquivo para que toDto gere a URL corretamente
        saved.setChave("capas-album/albums/1/cap.jpg");
        saved.setNomeArquivo("cap.jpg");

        // Simula a persistência da capa
        when(capaRepo.save(any())).thenReturn(saved);

        // Simula presigned URL
        when(s3.generatePresignedUrl(anyString(), anyString(), any())).thenReturn("http://signed-url/some/path?x=1");

        List<?> resp = service.criar(1L, List.of((MultipartFile) file));

        // Verificações
        assertFalse(resp.isEmpty());
        verify(s3, times(1))
                .upload(anyString(), anyString(), any(), anyLong(), anyString());
        verify(capaRepo, times(1)).save(any());

        // Verifica que o DTO retornado contém a URL pré-assinada, possivelmente substituída pela publicBaseUrl
        Object dto0 = resp.get(0);
        assertTrue(dto0 instanceof br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO);
        var dto = (br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO) dto0;
        // If publicBaseUrl is set in tests, the service will replace the base of the presigned URL by it
        assertEquals("http://localhost:9000/some/path?x=1", dto.getUrl());
    }

    /**
     * Testa a exclusão de uma capa inexistente.
     *
     * Não deve lançar erro nem tentar excluir
     * registros do repositório.
     */
    @Test
    void excluir_naoDeveDispararErroSeNaoExistir() {
        when(capaRepo.findById(99L)).thenReturn(Optional.empty());

        service.excluir(99L);

        verify(capaRepo, never()).delete(any());
    }

    @Test
    void excluir_deveRemoverDoS3EBanco() {
        CapaAlbum c = new CapaAlbum();
        c.setId(7L);
        c.setChave("capas-album/albums/1/test.jpg");

        when(capaRepo.findById(7L)).thenReturn(Optional.of(c));

        service.excluir(7L);

        verify(s3, times(1)).delete(eq("capas"), eq("capas-album/albums/1/test.jpg"));
        verify(capaRepo, times(1)).delete(eq(c));
    }
}
