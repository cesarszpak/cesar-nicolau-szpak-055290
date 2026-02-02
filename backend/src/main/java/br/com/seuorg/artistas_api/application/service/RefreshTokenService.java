package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.domain.entity.RefreshToken;
import br.com.seuorg.artistas_api.domain.entity.Usuario;
import br.com.seuorg.artistas_api.domain.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service responsável pelo gerenciamento de Refresh Tokens.
 * Realiza a criação, busca e remoção de tokens utilizados
 * para renovação de autenticação JWT.
 */
@Service
public class RefreshTokenService {

    /**
     * Repositório responsável pelas operações de persistência
     * do RefreshToken.
     */
    private final RefreshTokenRepository repository;

    /**
     * Quantidade de dias até a expiração do refresh token.
     */
    private final int refreshExpirationDays;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            @Value("${jwt.refresh-expiration-days:7}") int refreshExpirationDays
    ) {
        this.repository = repository;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    /**
     * Cria e persiste um novo refresh token para o usuário informado.
     *
     * @param usuario Usuário associado ao refresh token
     * @return RefreshToken criado e salvo no banco de dados
     */
    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario) {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);
        token.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        return repository.save(token);
    }

    /**
     * Busca um refresh token pelo valor do token.
     *
     * @param token Valor do refresh token
     * @return Optional contendo o RefreshToken, se encontrado
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    /**
     * Remove um refresh token com base no valor do token.
     *
     * @param token Valor do refresh token a ser removido
     */
    @Transactional
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }
}
