package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.ArtistaCreateDTO;
import br.com.seuorg.artistas_api.application.service.ArtistaService;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private final ArtistaRepository artistaRepository;
    private final ArtistaService artistaService;

    public ArtistaController(ArtistaRepository artistaRepository, ArtistaService artistaService) {
        this.artistaRepository = artistaRepository;
        this.artistaService = artistaService;
    }

    @GetMapping
    public List<Artista> listar() {
        return artistaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Artista> cadastrar(@RequestBody ArtistaCreateDTO dto) {
        Artista artista = artistaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(artista.getId())
                .toUri();
        return ResponseEntity.created(location).body(artista);
    }
}
