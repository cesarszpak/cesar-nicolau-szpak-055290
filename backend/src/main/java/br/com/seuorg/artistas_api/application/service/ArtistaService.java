package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.ArtistaCreateDTO;
import br.com.seuorg.artistas_api.application.dto.ArtistaResponseDTO;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtistaService {

    private final ArtistaRepository repository;
    private final AlbumRepository albumRepository;

    public ArtistaService(ArtistaRepository repository, AlbumRepository albumRepository) {
        this.repository = repository;
        this.albumRepository = albumRepository;
    }

    public ArtistaResponseDTO criar(ArtistaCreateDTO dto) {
        Artista artista = new Artista();
        artista.setNome(dto.getNome());
        artista.setCreatedAt(LocalDateTime.now());
        Artista saved = repository.save(artista);
        return convertToResponseDTO(saved);
    }

    public ArtistaResponseDTO obterPorId(Long id) {
        Artista artista = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));
        return convertToResponseDTO(artista);
    }

    public Page<ArtistaResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public Page<ArtistaResponseDTO> buscarPorNomeAscendente(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCaseAsc(nome, pageable)
                .map(this::convertToResponseDTO);
    }

    public Page<ArtistaResponseDTO> buscarPorNomeDescendente(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCaseDesc(nome, pageable)
                .map(this::convertToResponseDTO);
    }

    public List<ArtistaResponseDTO> buscarPorNomeAscendente(String nome) {
        return repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public List<ArtistaResponseDTO> buscarPorNomeDescendente(String nome) {
        return repository.findByNomeContainingIgnoreCaseOrderByNomeDesc(nome)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public ArtistaResponseDTO atualizar(Long id, ArtistaCreateDTO dto) {
        Artista artista = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));
        artista.setNome(dto.getNome());
        artista.setUpdatedAt(LocalDateTime.now());
        Artista updated = repository.save(artista);
        return convertToResponseDTO(updated);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Artista não encontrado");
        }
        repository.deleteById(id);
    }

    private ArtistaResponseDTO convertToResponseDTO(Artista artista) {
        Long albumCount = albumRepository.countByArtistaId(artista.getId());
        return new ArtistaResponseDTO(
                artista.getId(),
                artista.getNome(),
                albumCount,
                artista.getCreatedAt(),
                artista.getUpdatedAt()
        );
    }
}
