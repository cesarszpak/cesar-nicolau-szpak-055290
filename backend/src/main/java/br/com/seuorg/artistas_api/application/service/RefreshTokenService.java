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

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final int refreshExpirationDays;

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${jwt.refresh-expiration-days:7}") int refreshExpirationDays) {
        this.repository = repository;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario) {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);
        token.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        return repository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Transactional
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }
}
