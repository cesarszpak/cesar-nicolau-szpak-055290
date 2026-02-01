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
    private S3StorageService s3;

    /** Serviço a ser testado, com dependências mockadas */
    @InjectMocks
    private CapaAlbumService service;

    /**
     * Inicializa os mocks antes da execução de cada teste.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
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

        // Simula a persistência da capa
        when(capaRepo.save(any())).thenReturn(saved);

        List<?> resp = service.criar(1L, List.of((MultipartFile) file));

        // Verificações
        assertFalse(resp.isEmpty());
        verify(s3, times(1))
                .upload(anyString(), anyString(), any(), anyLong(), anyString());
        verify(capaRepo, times(1)).save(any());
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
}
