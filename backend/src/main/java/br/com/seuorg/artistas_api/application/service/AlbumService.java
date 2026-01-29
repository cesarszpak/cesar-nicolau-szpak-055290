package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.AlbumCreateDTO;
import br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO;
import br.com.seuorg.artistas_api.domain.entity.Album;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
        
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
        

@Service
public class AlbumService {

    private final AlbumRepository repository;
    private final ArtistaRepository artistaRepository;
    public AlbumService(AlbumRepository repository, ArtistaRepository artistaRepository) {
        this.repository = repository;
        this.artistaRepository = artistaRepository;
    }

    public AlbumResponseDTO criar(AlbumCreateDTO dto) {
        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));
        Album album = new Album();
        album.setNome(dto.getNome());
        album.setArtista(artista);
        album.setCreatedAt(LocalDateTime.now());

        Album saved = repository.save(album);
        return convertToResponseDTO(saved);
    }

    public AlbumResponseDTO obterPorId(Long id) {
        Album album = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album não encontrado"));
        return convertToResponseDTO(album);
    }

    public Page<AlbumResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public Page<AlbumResponseDTO> listarPorArtista(Long artistaId, Pageable pageable) {
        if (!artistaRepository.existsById(artistaId)) {
            throw new RuntimeException("Artista não encontrado");
        }
        return repository.findByArtistaId(artistaId, pageable)
                .map(this::convertToResponseDTO);
    }

    public Page<AlbumResponseDTO> listarPorTipo(Long tipoId, Pageable pageable) {
        throw new UnsupportedOperationException("Listar por tipo foi removido do sistema");
    }

    public Page<AlbumResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::convertToResponseDTO);
    }

    public AlbumResponseDTO atualizar(Long id, AlbumCreateDTO dto) {
        Album album = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album não encontrado"));

        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));
        album.setNome(dto.getNome());
        album.setArtista(artista);
        album.setUpdatedAt(LocalDateTime.now());

        Album updated = repository.save(album);
        return convertToResponseDTO(updated);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Album não encontrado");
        }
        repository.deleteById(id);
    }

    private AlbumResponseDTO convertToResponseDTO(Album album) {
        return new AlbumResponseDTO(
                album.getId(),
                album.getNome(),
                album.getArtista().getId(),
                album.getArtista().getNome(),
                
                album.getCreatedAt(),
                album.getUpdatedAt()
        );
    }
}
