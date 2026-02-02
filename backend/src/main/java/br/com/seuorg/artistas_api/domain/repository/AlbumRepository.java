package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Album.
 * Extende JpaRepository para fornecer CRUD básico e permite consultas customizadas.
 */
public interface AlbumRepository extends JpaRepository<Album, Long> {

    /**
     * Retorna todos os álbuns de um determinado artista.
     * @param artistaId ID do artista
     * @return Lista de álbuns do artista
     */
    List<Album> findByArtistaId(Long artistaId);

    /**
     * Retorna uma página de álbuns de um determinado artista.
     * @param artistaId ID do artista
     * @param pageable Objeto de paginação
     * @return Página de álbuns
     */
    Page<Album> findByArtistaId(Long artistaId, Pageable pageable);

    /**
     * Busca álbuns pelo nome, ignorando maiúsculas/minúsculas e permitindo correspondência parcial.
     * @param nome Nome ou parte do nome do álbum
     * @param pageable Objeto de paginação
     * @return Página de álbuns que correspondem ao nome
     */
    @Query("SELECT a FROM Album a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Album> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    /**
     * Conta quantos álbuns um determinado artista possui.
     * @param artistaId ID do artista
     * @return Quantidade de álbuns
     */
    @Query("SELECT COUNT(a) FROM Album a WHERE a.artista.id = :artistaId")
    Long countByArtistaId(@Param("artistaId") Long artistaId);
}
