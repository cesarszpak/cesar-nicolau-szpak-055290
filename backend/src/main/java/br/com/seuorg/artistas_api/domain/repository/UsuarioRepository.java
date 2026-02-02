package br.com.seuorg.artistas_api.domain.repository;

import br.com.seuorg.artistas_api.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Usuario.
 * Extende JpaRepository para fornecer CRUD básico.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     * @param email Email do usuário
     * @return Optional com o Usuário encontrado ou vazio se não existir
     */
    Optional<Usuario> findByEmail(String email);
}
