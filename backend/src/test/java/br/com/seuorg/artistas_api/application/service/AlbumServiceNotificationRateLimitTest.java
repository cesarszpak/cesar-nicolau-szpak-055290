package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.AlbumCreateDTO;
import br.com.seuorg.artistas_api.domain.entity.Artista;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.ArtistaRepository;
import br.com.seuorg.artistas_api.notification.NotificationRateLimiter;
import br.com.seuorg.artistas_api.websocket.AlbumNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class AlbumServiceNotificationRateLimitTest {

    private AlbumRepository albumRepository;
    private ArtistaRepository artistaRepository;
    private AlbumNotifier notifier;
    private AlbumService albumService;
    private NotificationRateLimiter limiter;

    @BeforeEach
    void setup() {
        albumRepository = mock(AlbumRepository.class);
        artistaRepository = mock(ArtistaRepository.class);
        notifier = mock(AlbumNotifier.class);
        limiter = new NotificationRateLimiter(2, 60); // small window for test
        albumService = new AlbumService(albumRepository, artistaRepository, notifier, limiter);
    }

    @Test
    void criar_should_not_notify_after_limit_exceeded() {
        var dto = new AlbumCreateDTO();
        dto.setNome("Novo");
        dto.setArtistaId(1L);

        var artista = new Artista();
        artista.setId(1L);
        artista.setNome("Artista");

        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));

        var saved = new br.com.seuorg.artistas_api.domain.entity.Album();
        saved.setId(42L);
        saved.setNome("Novo");
        saved.setArtista(artista);

        when(albumRepository.save(any())).thenReturn(saved);

        // Simula usuário autenticado no contexto
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("u1");
        var ctx = mock(org.springframework.security.core.context.SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        var holder = org.springframework.security.core.context.SecurityContextHolder.getContext();
        var previous = org.springframework.security.core.context.SecurityContextHolder.getContext();
        try {
            org.springframework.security.core.context.SecurityContextHolder.setContext(ctx);

            // duas criações -> notificações enviadas
            albumService.criar(dto);
            albumService.criar(dto);

            // terceira criação -> excede limite e não deve enviar
            albumService.criar(dto);

            ArgumentCaptor<br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO> captor = ArgumentCaptor.forClass(br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO.class);
            verify(notifier, times(2)).notifyNewAlbum(captor.capture());
            assertThat(captor.getAllValues()).hasSize(2);
        } finally {
            // limpa contexto
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }
}