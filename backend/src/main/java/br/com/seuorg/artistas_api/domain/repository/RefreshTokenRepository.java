package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade RefreshToken.
 * Extende JpaRepository para fornecer CRUD básico e consultas personalizadas.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Busca um token de refresh pelo seu valor e já carrega o usuário associado (fetch join).
     * @param token Token de refresh
     * @return Optional com o RefreshToken encontrado ou vazio se não existir
     */
    @Query("select r from RefreshToken r join fetch r.usuario where r.token = :token")
    Optional<RefreshToken> findByToken(@Param("token") String token);

    /**
     * Deleta um token de refresh pelo seu valor.
     * @param token Token de refresh a ser removido
     */
    void deleteByToken(String token);
}
