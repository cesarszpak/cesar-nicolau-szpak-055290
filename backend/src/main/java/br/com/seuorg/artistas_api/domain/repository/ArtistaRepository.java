package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
}
