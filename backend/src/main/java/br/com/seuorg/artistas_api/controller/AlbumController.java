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

@RestController
@RequestMapping("/api/albuns")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.obterPorId(id));
    }

    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorArtista(
            @PathVariable Long artistaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.listarPorArtista(artistaId, pageable));
    }

    @GetMapping("/buscar/nome")
    public ResponseEntity<Page<AlbumResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.buscarPorNome(nome, pageable));
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> cadastrar(@Valid @RequestBody AlbumCreateDTO dto) {
        AlbumResponseDTO album = albumService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(album.getId())
                .toUri();
        return ResponseEntity.created(location).body(album);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlbumCreateDTO dto) {
        return ResponseEntity.ok(albumService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        albumService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
