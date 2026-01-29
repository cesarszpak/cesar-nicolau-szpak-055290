package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtistaId(Long artistaId);
    
    Page<Album> findByArtistaId(Long artistaId, Pageable pageable);
    
    
    @Query("SELECT a FROM Album a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Album> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Album a WHERE a.artista.id = :artistaId")
    Long countByArtistaId(@Param("artistaId") Long artistaId);
}
