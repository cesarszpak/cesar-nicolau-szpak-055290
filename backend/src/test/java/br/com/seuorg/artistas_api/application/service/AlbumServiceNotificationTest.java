package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.AlbumCreateDTO;
import br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO;
import br.com.seuorg.artistas_api.domain.entity.Album;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import br.com.seuorg.artistas_api.websocket.AlbumNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Teste unitário responsável por validar se o serviço de criação
 * de álbuns notifica corretamente os clientes via WebSocket
 * após o cadastro de um novo álbum.
 */
class AlbumServiceNotificationTest {

    // Repositório de álbuns (mockado)
    private AlbumRepository albumRepository;

    // Repositório de artistas (mockado)
    private ArtistaRepository artistaRepository;

    // Notificador WebSocket (mockado)
    private AlbumNotifier notifier;

    // Serviço que será testado
    private AlbumService albumService;

    /**
     * Configuração inicial executada antes de cada teste.
     * Inicializa os mocks e instancia o serviço.
     */
    @BeforeEach
    void setup() {
        albumRepository = mock(AlbumRepository.class);
        artistaRepository = mock(ArtistaRepository.class);
        notifier = mock(AlbumNotifier.class);
        var limiter = new br.com.seuorg.artistas_api.notification.NotificationRateLimiter(100, 60);

        // Injeta os mocks no serviço (limiter com alto limite para não interferir no teste)
        albumService = new AlbumService(albumRepository, artistaRepository, notifier, limiter);
    }

    /**
     * Deve notificar via WebSocket quando um novo álbum
     * for criado com sucesso.
     */
    @Test
    void criar_should_notify_when_album_created() {

        // DTO de entrada para criação do álbum
        var dto = new AlbumCreateDTO();
        dto.setNome("Novo");
        dto.setArtistaId(1L);

        // Artista existente no sistema
        var artista = new Artista();
        artista.setId(1L);
        artista.setNome("Artista");

        // Simula a busca do artista no repositório
        when(artistaRepository.findById(1L))
                .thenReturn(Optional.of(artista));

        // Álbum salvo no banco de dados
        var saved = new Album();
        saved.setId(42L);
        saved.setNome("Novo");
        saved.setArtista(artista);
        saved.setCreatedAt(LocalDateTime.now());

        // Simula a persistência do álbum
        when(albumRepository.save(any()))
                .thenReturn(saved);

        // Executa o método de criação
        AlbumResponseDTO result = albumService.criar(dto);

        // Verifica se o álbum retornado possui o ID esperado
        assertThat(result.getId()).isEqualTo(42L);

        // Captura o DTO enviado para o notificador
        ArgumentCaptor<AlbumResponseDTO> captor =
                ArgumentCaptor.forClass(AlbumResponseDTO.class);

        // Verifica se o método de notificação foi chamado exatamente uma vez
        verify(notifier, times(1)).notifyNewAlbum(captor.capture());

        // Obtém o DTO enviado na notificação
        AlbumResponseDTO sent = captor.getValue();

        // Valida se o nome do álbum notificado está correto
        assertThat(sent.getNome()).isEqualTo("Novo");
    }
}
