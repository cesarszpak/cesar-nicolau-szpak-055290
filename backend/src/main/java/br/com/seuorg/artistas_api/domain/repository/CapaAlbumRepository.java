package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.CapaAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório responsável pelas operações de persistência
 * da entidade CapaAlbum.
 *
 * Utiliza o Spring Data JPA para fornecer métodos padrão
 * de CRUD e consultas customizadas.
 */
public interface CapaAlbumRepository extends JpaRepository<CapaAlbum, Long> {

    /**
     * Recupera todas as capas associadas a um álbum específico.
     *
     * @param albumId identificador do álbum
     * @return lista de capas do álbum
     */
    List<CapaAlbum> findByAlbumId(Long albumId);
}
