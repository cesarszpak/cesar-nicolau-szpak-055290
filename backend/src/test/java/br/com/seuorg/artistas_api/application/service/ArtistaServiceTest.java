package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.ArtistaCreateDTO;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço de artistas (ArtistaService).
 *
 * Valida o comportamento das regras de negócio relacionadas
 * à criação, listagem e obtenção de artistas.
 */
class ArtistaServiceTest {

    /** Mock do repositório de artistas */
    @Mock
    private ArtistaRepository artistaRepository;

    /** Mock do repositório de álbuns */
    @Mock
    private AlbumRepository albumRepository;

    /** Serviço a ser testado, com dependências mockadas */
    @InjectMocks
    private ArtistaService service;

    /**
     * Inicializa os mocks antes da execução de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa a criação de um artista com dados válidos.
     *
     * Deve salvar o artista no repositório e retornar
     * o objeto com o ID preenchido.
     */
    @Test
    void criar_deveSalvarArtista() {
        ArtistaCreateDTO dto = new ArtistaCreateDTO();
        dto.setNome("Novo");

        Artista saved = new Artista();
        saved.setId(1L);
        saved.setNome("Novo");

        // Configura o comportamento do mock
        when(artistaRepository.save(any())).thenReturn(saved);

        var result = service.criar(dto);

        // Verificações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(artistaRepository, times(1)).save(any());
    }

    /**
     * Testa a listagem paginada de artistas.
     *
     * Deve retornar uma página de artistas.
     */
    @Test
    void listarTodos_deveRetornarPagina() {
        when(artistaRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        var page = service.listarTodos(PageRequest.of(0, 10));

        assertNotNull(page);
        verify(artistaRepository, times(1))
                .findAll(PageRequest.of(0, 10));
    }

    /**
     * Testa a obtenção de um artista por ID quando ele não existe.
     *
     * Deve lançar uma exceção ao tentar buscar
     * um artista inexistente.
     */
    @Test
    void obterPorId_artistaNaoEncontrado_deveLancar() {
        when(artistaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.obterPorId(99L));
    }
}
