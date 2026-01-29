package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    List<Artista> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
    
    List<Artista> findByNomeContainingIgnoreCaseOrderByNomeDesc(String nome);
    
    Page<Artista> findAll(Pageable pageable);
    
    @Query("SELECT a FROM Artista a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY a.nome ASC")
    Page<Artista> findByNomeContainingIgnoreCaseAsc(@Param("nome") String nome, Pageable pageable);
    
    @Query("SELECT a FROM Artista a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY a.nome DESC")
    Page<Artista> findByNomeContainingIgnoreCaseDesc(@Param("nome") String nome, Pageable pageable);
}
