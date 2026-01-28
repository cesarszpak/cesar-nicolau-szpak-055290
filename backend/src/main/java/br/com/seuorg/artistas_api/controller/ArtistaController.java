package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private final ArtistaRepository artistaRepository;

    public ArtistaController(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    @GetMapping
    public List<Artista> listar() {
        return artistaRepository.findAll();
    }
}
