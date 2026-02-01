package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.AlbumCreateDTO;
import br.com.seuorg.artistas_api.domain.entity.Album;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço de álbuns (AlbumService).
 *
 * Verifica o comportamento das regras de negócio relacionadas
 * à criação e listagem de álbuns.
 */
class AlbumServiceTest {

    /** Mock do repositório de álbuns */
    @Mock
    private AlbumRepository albumRepository;

    /** Mock do repositório de artistas */
    @Mock
    private ArtistaRepository artistaRepository;

    /** Serviço a ser testado, com dependências mockadas */
    @InjectMocks
    private AlbumService service;

    /**
     * Inicializa os mocks antes da execução de cada teste.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa a criação de um álbum com dados válidos.
     *
     * Deve criar o álbum e retornar o objeto com o ID preenchido.
     */
    @Test
    void criar_deveCriarAlbum() {
        AlbumCreateDTO dto = new AlbumCreateDTO();
        dto.setNome("Novo Album");
        dto.setArtistaId(1L);

        Artista a = new Artista();
        a.setId(1L);
        a.setNome("Artista");

        Album saved = new Album();
        saved.setId(2L);
        saved.setNome("Novo Album");
        saved.setArtista(a);

        // Configura o comportamento dos mocks
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(a));
        when(albumRepository.save(any())).thenReturn(saved);

        var resp = service.criar(dto);

        // Verifica se o álbum foi criado corretamente
        assertEquals(2L, resp.getId());
    }

    /**
     * Testa a listagem de álbuns quando o artista não existe.
     *
     * Deve lançar uma exceção ao tentar listar álbuns
     * de um artista inexistente.
     */
    @Test
    void listarPorArtista_artistaNaoExiste_deveLancar() {
        when(artistaRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> service.listarPorArtista(99L, PageRequest.of(0, 10))
        );
    }
}
