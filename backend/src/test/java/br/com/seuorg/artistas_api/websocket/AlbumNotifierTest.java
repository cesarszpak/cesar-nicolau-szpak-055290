package br.com.seuorg.artistas_api.websocket;

import br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.*;

/**
 * Teste unitário responsável por validar o envio de mensagens
 * WebSocket quando um novo álbum é notificado.
 */
class AlbumNotifierTest {

    /**
     * Deve enviar uma mensagem de texto para todas as sessões
     * WebSocket que estiverem abertas.
     */
    @Test
    void notifyNewAlbum_sends_text_message_to_open_sessions() throws Exception {

        // Instancia o notificador de álbuns
        AlbumNotifier notifier = new AlbumNotifier();

        // Cria uma sessão WebSocket mockada
        WebSocketSession session = mock(WebSocketSession.class);

        // Simula que a sessão está aberta
        when(session.isOpen()).thenReturn(true);

        // Registra a sessão no notificador
        notifier.register(session);

        // DTO representando um novo álbum criado
        AlbumResponseDTO dto =
                new AlbumResponseDTO(1L, "Nome", 2L, "Artista", null, null);

        // Executa a notificação do novo álbum
        notifier.notifyNewAlbum(dto);

        // Verifica se a mensagem foi enviada ao menos uma vez
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
    }
}
