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

/**
 * Serviço responsável pelas regras de negócio relacionadas à entidade Álbum.
 *
 * Esta classe realiza operações de criação, consulta, listagem,
 * atualização e exclusão de álbuns, além de validar a existência
 * de artistas associados.
 */
@Service
public class AlbumService {

    /** Repositório de persistência de álbuns */
    private final AlbumRepository repository;

    /** Repositório de persistência de artistas */
    private final ArtistaRepository artistaRepository;

    /** Notificador de novos álbuns via WebSocket */
    private final br.com.seuorg.artistas_api.websocket.AlbumNotifier albumNotifier;

    /** Rate limiter de notificações por usuário */
    private final br.com.seuorg.artistas_api.notification.NotificationRateLimiter notificationLimiter;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository repositório de álbuns
     * @param artistaRepository repositório de artistas
     */
    public AlbumService(AlbumRepository repository, ArtistaRepository artistaRepository, br.com.seuorg.artistas_api.websocket.AlbumNotifier albumNotifier, br.com.seuorg.artistas_api.notification.NotificationRateLimiter notificationLimiter) {
        this.repository = repository;
        this.artistaRepository = artistaRepository;
        this.albumNotifier = albumNotifier;
        this.notificationLimiter = notificationLimiter;
    }

    /**
     * Cria um novo álbum vinculado a um artista.
     *
     * @param dto dados necessários para criação do álbum
     * @return álbum criado
     */
    public AlbumResponseDTO criar(AlbumCreateDTO dto) {
        // Busca o artista pelo ID informado
        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        // Cria a entidade Álbum
        Album album = new Album();
        album.setNome(dto.getNome());
        album.setArtista(artista);
        album.setCreatedAt(LocalDateTime.now());

        // Salva o álbum no banco de dados
        Album saved = repository.save(album);

        // Notifica clientes conectados via WebSocket sobre novo álbum
        try {
            // Identifica usuário autenticado, se houver
            String user = null;
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                user = auth.getName();
            }

            boolean allowed = true;
            if (user != null) {
                allowed = notificationLimiter.tryAcquire("user:" + user);
            } else {
                // fallback global limiter per anonymous (ip not available here)
                allowed = notificationLimiter.tryAcquire("anonymous");
            }

            if (allowed) {
                albumNotifier.notifyNewAlbum(convertToResponseDTO(saved));
            } else {
                // Quando excede, não enviamos a notificação; registro para auditoria
                org.slf4j.LoggerFactory.getLogger(AlbumService.class).warn("Notification rate limit exceeded for user {}", user);
            }
        } catch (Exception e) {
            // Não impedimos a criação do álbum por causa de falha na notificação
        }

        // Converte a entidade para DTO de resposta
        return convertToResponseDTO(saved);
    }

    /**
     * Obtém um álbum pelo seu ID.
     *
     * @param id identificador do álbum
     * @return álbum encontrado
     */
    public AlbumResponseDTO obterPorId(Long id) {
        Album album = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album não encontrado"));

        return convertToResponseDTO(album);
    }

    /**
     * Lista todos os álbuns de forma paginada.
     *
     * @param pageable dados de paginação
     * @return página de álbuns
     */
    public Page<AlbumResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Lista os álbuns de um artista específico.
     *
     * @param artistaId identificador do artista
     * @param pageable dados de paginação
     * @return página de álbuns do artista
     */
    public Page<AlbumResponseDTO> listarPorArtista(Long artistaId, Pageable pageable) {
        // Verifica se o artista existe
        if (!artistaRepository.existsById(artistaId)) {
            throw new RuntimeException("Artista não encontrado");
        }

        return repository.findByArtistaId(artistaId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Busca álbuns pelo nome, ignorando maiúsculas e minúsculas.
     *
     * @param nome nome ou parte do nome do álbum
     * @param pageable dados de paginação
     * @return página de álbuns encontrados
     */
    public Page<AlbumResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Atualiza os dados de um álbum existente.
     *
     * @param id identificador do álbum
     * @param dto novos dados do álbum
     * @return álbum atualizado
     */
    public AlbumResponseDTO atualizar(Long id, AlbumCreateDTO dto) {
        // Busca o álbum pelo ID
        Album album = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album não encontrado"));

        // Busca o artista associado
        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        // Atualiza os dados do álbum
        album.setNome(dto.getNome());
        album.setArtista(artista);
        album.setUpdatedAt(LocalDateTime.now());

        // Salva as alterações
        Album updated = repository.save(album);

        return convertToResponseDTO(updated);
    }

    /**
     * Remove um álbum pelo seu ID.
     *
     * @param id identificador do álbum
     */
    public void deletar(Long id) {
        // Verifica se o álbum existe antes de remover
        if (!repository.existsById(id)) {
            throw new RuntimeException("Album não encontrado");
        }

        repository.deleteById(id);
    }

    /**
     * Converte a entidade Album para o DTO de resposta.
     *
     * @param album entidade álbum
     * @return DTO de resposta
     */
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
