package br.com.seuorg.artistas_api.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Classe de configuração responsável por habilitar e registrar
 * os endpoints WebSocket da aplicação.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Handler responsável por gerenciar as conexões WebSocket de álbuns
    private final AlbumWebSocketHandler handler;

    // URL do frontend autorizada a se conectar via WebSocket
    private final String frontendUrl;

    /**
     * Construtor da configuração WebSocket.
     *
     * @param handler handler que processa as conexões WebSocket
     * @param frontendUrl URL do frontend permitida (vinda do application.properties)
     */
    public WebSocketConfig(
            AlbumWebSocketHandler handler,
            @Value("${frontend.url:http://localhost:3000}") String frontendUrl
    ) {
        this.handler = handler;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Registra os handlers WebSocket da aplicação.
     *
     * Define o endpoint "/ws/albums" e restringe
     * as origens permitidas para conexão.
     *
     * @param registry registro de handlers WebSocket
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            // Endpoint WebSocket utilizado para notificações de novos álbuns
            .addHandler(handler, "/ws/albums")

            // Define a origem permitida para conexão (CORS WebSocket)
            .setAllowedOrigins(frontendUrl);
    }
}
