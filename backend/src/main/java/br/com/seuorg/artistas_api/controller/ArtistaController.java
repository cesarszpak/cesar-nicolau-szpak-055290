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

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping
    public ResponseEntity<Page<ArtistaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(artistaService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(artistaService.obterPorId(id));
    }

    @GetMapping("/buscar/nome")
    public ResponseEntity<Page<ArtistaResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "asc") String ordem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if ("desc".equalsIgnoreCase(ordem)) {
            return ResponseEntity.ok(artistaService.buscarPorNomeDescendente(nome, pageable));
        }
        return ResponseEntity.ok(artistaService.buscarPorNomeAscendente(nome, pageable));
    }

    @PostMapping
    public ResponseEntity<ArtistaResponseDTO> cadastrar(@Valid @RequestBody ArtistaCreateDTO dto) {
        ArtistaResponseDTO artista = artistaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(artista.getId())
                .toUri();
        return ResponseEntity.created(location).body(artista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ArtistaCreateDTO dto) {
        return ResponseEntity.ok(artistaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        artistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
