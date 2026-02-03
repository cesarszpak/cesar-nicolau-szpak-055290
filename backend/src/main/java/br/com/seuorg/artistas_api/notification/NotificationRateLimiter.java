package br.com.seuorg.artistas_api.notification;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe responsável por limitar a quantidade de notificações
 * enviadas por usuário dentro de uma janela de tempo definida.
 *
 * Evita que um mesmo usuário receba muitas notificações em um
 * curto período, funcionando como um rate limiter simples
 * baseado em contagem.
 */
public class NotificationRateLimiter {

    /**
     * Número máximo de notificações permitidas dentro da janela de tempo
     */
    private final int maxNotifications;

    /**
     * Duração da janela de tempo, em segundos
     */
    private final int windowSeconds;

    /**
     * Representa a janela de contagem de um usuário específico
     */
    private static class Window {

        /**
         * Instante (em segundos) em que a janela atual iniciou
         */
        volatile long windowStart;

        /**
         * Contador de notificações enviadas dentro da janela atual
         */
        AtomicInteger counter = new AtomicInteger(0);
    }

    /**
     * Mapa que armazena as janelas de contagem por usuário
     */
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    /**
     * Cria um novo rate limiter de notificações.
     *
     * @param maxNotifications número máximo de notificações permitidas
     * @param windowSeconds duração da janela de tempo em segundos
     */
    public NotificationRateLimiter(int maxNotifications, int windowSeconds) {
        this.maxNotifications = maxNotifications;
        this.windowSeconds = windowSeconds;
    }

    /**
     * Tenta adquirir permissão para enviar uma notificação ao usuário.
     *
     * Se o número de notificações enviadas dentro da janela atual
     * for menor ou igual ao limite configurado, a notificação é permitida.
     * Caso contrário, a notificação deve ser bloqueada.
     *
     * @param userKey identificador único do usuário
     * @return true se a notificação pode ser enviada, false caso contrário
     */
    public synchronized boolean tryAcquire(String userKey) {
        long now = Instant.now().getEpochSecond();

        // Obtém ou cria a janela de contagem do usuário
        Window w = windows.computeIfAbsent(userKey, k -> {
            Window nw = new Window();
            nw.windowStart = now;
            return nw;
        });

        synchronized (w) {
            // Se a janela expirou, reinicia o contador
            if (now - w.windowStart >= windowSeconds) {
                w.windowStart = now;
                w.counter.set(0);
            }

            // Incrementa o contador e verifica se está dentro do limite
            int current = w.counter.incrementAndGet();
            return current <= maxNotifications;
        }
    }
}
