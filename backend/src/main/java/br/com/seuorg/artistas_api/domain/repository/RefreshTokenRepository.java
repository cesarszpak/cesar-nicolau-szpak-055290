package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("select r from RefreshToken r join fetch r.usuario where r.token = :token")
    Optional<RefreshToken> findByToken(@Param("token") String token);

    void deleteByToken(String token);
}