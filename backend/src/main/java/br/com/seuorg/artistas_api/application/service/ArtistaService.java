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

/**
 * Serviço responsável pelas regras de negócio relacionadas à entidade Artista.
 *
 * Esta classe gerencia a criação, consulta, listagem, busca,
 * atualização e exclusão de artistas, além de calcular a
 * quantidade de álbuns associados a cada artista.
 */
@Service
public class ArtistaService {

    /** Repositório de persistência de artistas */
    private final ArtistaRepository repository;

    /** Repositório de álbuns, utilizado para contagem de álbuns por artista */
    private final AlbumRepository albumRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository repositório de artistas
     * @param albumRepository repositório de álbuns
     */
    public ArtistaService(ArtistaRepository repository, AlbumRepository albumRepository) {
        this.repository = repository;
        this.albumRepository = albumRepository;
    }

    /**
     * Cria um novo artista.
     *
     * @param dto dados necessários para criação do artista
     * @return artista criado
     */
    public ArtistaResponseDTO criar(ArtistaCreateDTO dto) {
        // Cria a entidade Artista
        Artista artista = new Artista();
        artista.setNome(dto.getNome());
        artista.setCreatedAt(LocalDateTime.now());

        // Salva o artista no banco de dados
        Artista saved = repository.save(artista);

        // Converte a entidade para DTO de resposta
        return convertToResponseDTO(saved);
    }

    /**
     * Obtém um artista pelo seu ID.
     *
     * @param id identificador do artista
     * @return artista encontrado
     */
    public ArtistaResponseDTO obterPorId(Long id) {
        Artista artista = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        return convertToResponseDTO(artista);
    }

    /**
     * Lista todos os artistas de forma paginada.
     *
     * @param pageable dados de paginação
     * @return página de artistas
     */
    public Page<ArtistaResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Busca artistas pelo nome em ordem ascendente, com paginação.
     *
     * @param nome nome ou parte do nome do artista
     * @param pageable dados de paginação
     * @return página de artistas encontrados
     */
    public Page<ArtistaResponseDTO> buscarPorNomeAscendente(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCaseAsc(nome, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Busca artistas pelo nome em ordem descendente, com paginação.
     *
     * @param nome nome ou parte do nome do artista
     * @param pageable dados de paginação
     * @return página de artistas encontrados
     */
    public Page<ArtistaResponseDTO> buscarPorNomeDescendente(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCaseDesc(nome, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Busca artistas pelo nome em ordem ascendente, sem paginação.
     *
     * @param nome nome ou parte do nome do artista
     * @return lista de artistas encontrados
     */
    public List<ArtistaResponseDTO> buscarPorNomeAscendente(String nome) {
        return repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Busca artistas pelo nome em ordem descendente, sem paginação.
     *
     * @param nome nome ou parte do nome do artista
     * @return lista de artistas encontrados
     */
    public List<ArtistaResponseDTO> buscarPorNomeDescendente(String nome) {
        return repository.findByNomeContainingIgnoreCaseOrderByNomeDesc(nome)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Atualiza os dados de um artista existente.
     *
     * @param id identificador do artista
     * @param dto novos dados do artista
     * @return artista atualizado
     */
    public ArtistaResponseDTO atualizar(Long id, ArtistaCreateDTO dto) {
        // Busca o artista pelo ID
        Artista artista = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        // Atualiza os dados do artista
        artista.setNome(dto.getNome());
        artista.setUpdatedAt(LocalDateTime.now());

        // Salva as alterações
        Artista updated = repository.save(artista);

        return convertToResponseDTO(updated);
    }

    /**
     * Remove um artista pelo seu ID.
     *
     * @param id identificador do artista
     */
    public void deletar(Long id) {
        // Verifica se o artista existe antes de remover
        if (!repository.existsById(id)) {
            throw new RuntimeException("Artista não encontrado");
        }

        repository.deleteById(id);
    }

    /**
     * Converte a entidade Artista para o DTO de resposta.
     * Também calcula a quantidade de álbuns associados ao artista.
     *
     * @param artista entidade artista
     * @return DTO de resposta
     */
    private ArtistaResponseDTO convertToResponseDTO(Artista artista) {
        // Conta quantos álbuns o artista possui
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
