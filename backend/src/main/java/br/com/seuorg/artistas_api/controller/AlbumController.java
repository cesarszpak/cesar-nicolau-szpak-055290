package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.AlbumCreateDTO;
import br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO;
import br.com.seuorg.artistas_api.application.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller responsável por expor os endpoints REST
 * relacionados à entidade Álbum.
 */
@RestController
@RequestMapping("/api/albuns")
public class AlbumController {

    /**
     * Service responsável pelas regras de negócio
     * relacionadas aos álbuns.
     */
    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * Lista todos os álbuns de forma paginada.
     *
     * @param page Número da página (default 0)
     * @param size Quantidade de registros por página (default 10)
     * @return Página de álbuns
     */
    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.listarTodos(pageable));
    }

    /**
     * Obtém um álbum pelo seu ID.
     *
     * @param id Identificador do álbum
     * @return Dados do álbum
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.obterPorId(id));
    }

    /**
     * Lista os álbuns de um artista específico de forma paginada.
     *
     * @param artistaId ID do artista
     * @param page Número da página
     * @param size Quantidade de registros por página
     * @return Página de álbuns do artista
     */
    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorArtista(
            @PathVariable Long artistaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.listarPorArtista(artistaId, pageable));
    }

    /**
     * Busca álbuns pelo nome de forma paginada.
     *
     * @param nome Nome (ou parte do nome) do álbum
     * @param page Número da página
     * @param size Quantidade de registros por página
     * @return Página de álbuns encontrados
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<Page<AlbumResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.buscarPorNome(nome, pageable));
    }

    /**
     * Cadastra um novo álbum.
     *
     * @param dto Dados do álbum a ser criado
     * @return Álbum criado com status 201 e header Location
     */
    @PostMapping
    public ResponseEntity<AlbumResponseDTO> cadastrar(@Valid @RequestBody AlbumCreateDTO dto) {
        AlbumResponseDTO album = albumService.criar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(album.getId())
                .toUri();

        return ResponseEntity.created(location).body(album);
    }

    /**
     * Atualiza os dados de um álbum existente.
     *
     * @param id ID do álbum
     * @param dto Novos dados do álbum
     * @return Álbum atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlbumCreateDTO dto) {
        return ResponseEntity.ok(albumService.atualizar(id, dto));
    }

    /**
     * Remove um álbum pelo seu ID.
     *
     * @param id ID do álbum a ser removido
     * @return Resposta sem conteúdo (204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        albumService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
