package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.ArtistaCreateDTO;
import br.com.seuorg.artistas_api.application.dto.ArtistaResponseDTO;
import br.com.seuorg.artistas_api.application.service.ArtistaService;
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
 * relacionados à entidade Artista.
 */
@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    /**
     * Service responsável pelas regras de negócio
     * relacionadas aos artistas.
     */
    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    /**
     * Lista todos os artistas de forma paginada.
     *
     * @param page Número da página (default 0)
     * @param size Quantidade de registros por página (default 10)
     * @return Página de artistas
     */
    @GetMapping
    public ResponseEntity<Page<ArtistaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(artistaService.listarTodos(pageable));
    }

    /**
     * Obtém um artista pelo seu ID.
     *
     * @param id Identificador do artista
     * @return Dados do artista
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(artistaService.obterPorId(id));
    }

    /**
     * Busca artistas pelo nome, permitindo definir a ordenação
     * ascendente ou descendente.
     *
     * @param nome Nome (ou parte do nome) do artista
     * @param ordem Ordem da listagem (asc ou desc)
     * @param page Número da página
     * @param size Quantidade de registros por página
     * @return Página de artistas encontrados
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<Page<ArtistaResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "asc") String ordem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if ("desc".equalsIgnoreCase(ordem)) {
            return ResponseEntity.ok(
                    artistaService.buscarPorNomeDescendente(nome, pageable)
            );
        }

        return ResponseEntity.ok(
                artistaService.buscarPorNomeAscendente(nome, pageable)
        );
    }

    /**
     * Cadastra um novo artista.
     *
     * @param dto Dados do artista a ser criado
     * @return Artista criado com status 201 e header Location
     */
    @PostMapping
    public ResponseEntity<ArtistaResponseDTO> cadastrar(
            @Valid @RequestBody ArtistaCreateDTO dto) {

        ArtistaResponseDTO artista = artistaService.criar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(artista.getId())
                .toUri();

        return ResponseEntity.created(location).body(artista);
    }

    /**
     * Atualiza os dados de um artista existente.
     *
     * @param id ID do artista
     * @param dto Novos dados do artista
     * @return Artista atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ArtistaCreateDTO dto) {
        return ResponseEntity.ok(artistaService.atualizar(id, dto));
    }

    /**
     * Remove um artista pelo seu ID.
     *
     * @param id ID do artista a ser removido
     * @return Resposta sem conteúdo (204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        artistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
