package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Artista.
 * Extende JpaRepository para fornecer CRUD básico e consultas personalizadas.
 */
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    /**
     * Busca artistas pelo nome (case-insensitive) e retorna a lista ordenada por nome ascendente.
     * @param nome Nome ou parte do nome do artista
     * @return Lista de artistas ordenada ascendentemente
     */
    List<Artista> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    /**
     * Busca artistas pelo nome (case-insensitive) e retorna a lista ordenada por nome descendente.
     * @param nome Nome ou parte do nome do artista
     * @return Lista de artistas ordenada descendentemente
     */
    List<Artista> findByNomeContainingIgnoreCaseOrderByNomeDesc(String nome);

    /**
     * Retorna todos os artistas paginados.
     * @param pageable Objeto de paginação
     * @return Página de artistas
     */
    Page<Artista> findAll(Pageable pageable);

    /**
     * Busca artistas pelo nome (case-insensitive) e retorna uma página ordenada por nome ascendente.
     * @param nome Nome ou parte do nome do artista
     * @param pageable Objeto de paginação
     * @return Página de artistas ordenada ascendentemente
     */
    @Query("SELECT a FROM Artista a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY a.nome ASC")
    Page<Artista> findByNomeContainingIgnoreCaseAsc(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca artistas pelo nome (case-insensitive) e retorna uma página ordenada por nome descendente.
     * @param nome Nome ou parte do nome do artista
     * @param pageable Objeto de paginação
     * @return Página de artistas ordenada descendentemente
     */
    @Query("SELECT a FROM Artista a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY a.nome DESC")
    Page<Artista> findByNomeContainingIgnoreCaseDesc(@Param("nome") String nome, Pageable pageable);
}
