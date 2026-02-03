package br.com.seuorg.artistas_api.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handler WebSocket responsável por gerenciar o ciclo de vida
 * das conexões relacionadas à notificação de novos álbuns.
 */
@Component
public class AlbumWebSocketHandler extends TextWebSocketHandler {

    // Logger utilizado para registrar eventos e mensagens do WebSocket
    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumWebSocketHandler.class);

    // Componente responsável por registrar sessões e enviar notificações
    private final AlbumNotifier notifier;

    /**
     * Construtor com injeção do AlbumNotifier.
     *
     * @param notifier componente responsável pelo envio das notificações
     */
    public AlbumWebSocketHandler(AlbumNotifier notifier) {
        this.notifier = notifier;
    }

    /**
     * Executado quando uma nova conexão WebSocket é estabelecida.
     *
     * @param session sessão WebSocket recém-conectada
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Registra a sessão para receber notificações
        notifier.register(session);

        LOGGER.debug("Conexão WebSocket estabelecida: {}", session.getId());
    }

    /**
     * Executado quando uma conexão WebSocket é encerrada.
     *
     * @param session sessão WebSocket encerrada
     * @param status status do fechamento da conexão
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove a sessão do conjunto de sessões ativas
        notifier.unregister(session);

        LOGGER.debug("Conexão WebSocket encerrada: {}", session.getId());
    }

    /**
     * Manipula mensagens de texto enviadas pelo cliente.
     *
     * Atualmente não há processamento de mensagens do cliente,
     * sendo este método utilizado apenas para fins de log.
     *
     * @param session sessão WebSocket que enviou a mensagem
     * @param message mensagem recebida
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOGGER.debug(
            "Mensagem recebida da sessão {}: {}",
            session.getId(),
            message.getPayload()
        );
    }
}
