package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.ArtistaCreateDTO;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import org.springframework.stereotype.Service;

@Service
public class ArtistaService {

    private final ArtistaRepository repository;

    public ArtistaService(ArtistaRepository repository) {
        this.repository = repository;
    }

    public Artista criar(ArtistaCreateDTO dto) {
        Artista artista = new Artista();
        artista.setNome(dto.getNome());
        return repository.save(artista);
    }
}
