package br.com.seuorg.artistas_api.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.seuorg.artistas_api.application.dto.AlbumResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Componente responsável por gerenciar sessões WebSocket
 * e notificar os clientes conectados quando um novo álbum
 * é cadastrado no sistema.
 */
@Component
public class AlbumNotifier {

    // Logger utilizado para registrar eventos e erros relacionados ao WebSocket
    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumNotifier.class);

    // Conjunto de sessões WebSocket ativas (thread-safe)
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    // Mapper responsável por serializar o DTO para JSON
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Registra uma nova sessão WebSocket.
     *
     * @param session sessão WebSocket recém-conectada
     */
    public void register(WebSocketSession session) {
        sessions.add(session);
        LOGGER.debug("Sessão WebSocket registrada: {}", session.getId());
    }

    /**
     * Remove uma sessão WebSocket do conjunto de sessões ativas.
     *
     * @param session sessão WebSocket que foi desconectada
     */
    public void unregister(WebSocketSession session) {
        sessions.remove(session);
        LOGGER.debug("Sessão WebSocket removida: {}", session.getId());
    }

    /**
     * Notifica todos os clientes conectados sobre o cadastro
     * de um novo álbum.
     *
     * @param dto dados do álbum recém-criado
     */
    public void notifyNewAlbum(AlbumResponseDTO dto) {
        try {
            // Converte o DTO do álbum para JSON
            String payload = mapper.writeValueAsString(dto);
            TextMessage msg = new TextMessage(payload);

            // Envia a mensagem para todas as sessões abertas
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.sendMessage(msg);
                    } catch (Exception e) {
                        // Erro ao enviar mensagem para uma sessão específica
                        LOGGER.warn(
                            "Falha ao enviar mensagem para a sessão WebSocket {}",
                            s.getId(),
                            e
                        );
                    }
                }
            }
        } catch (Exception e) {
            // Erro ao serializar o DTO do álbum
            LOGGER.error("Falha ao serializar os dados do álbum para JSON", e);
        }
    }
}
