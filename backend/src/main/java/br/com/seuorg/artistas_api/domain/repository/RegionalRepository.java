package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável pelo acesso aos dados da entidade Regional.
 * Fornece métodos de consulta personalizados além das operações padrão do JPA.
 */
@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    /**
     * Retorna todas as regionais ativas.
     */
    List<Regional> findByAtivoTrue();

    /**
     * Busca regionais ativas pelo identificador externo.
     */
    List<Regional> findByExternalIdAndAtivoTrue(Integer externalId);

    /**
     * Busca uma regional ativa pelo identificador externo e nome.
     */
    Optional<Regional> findByExternalIdAndNomeAndAtivoTrue(Integer externalId, String nome);
}
