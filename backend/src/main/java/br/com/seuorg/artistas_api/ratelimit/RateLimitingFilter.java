package br.com.seuorg.artistas_api.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro responsável por aplicar rate limit por usuário autenticado
 * ou por endereço IP.
 *
 * O limite de requisições e a janela de tempo são configuráveis
 * via construtor.
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    // Logger utilizado para registrar eventos de estouro de limite
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Quantidade máxima de requisições permitidas dentro da janela de tempo
    private final int requestsPerWindow;

    // Duração da janela de tempo em segundos
    private final int windowSeconds;

    /**
     * Classe interna que representa uma janela de tempo
     * associada a um usuário ou endereço IP.
     */
    private static class Window {

        // Momento inicial da janela (em segundos desde o epoch)
        volatile long windowStart;

        // Contador de requisições realizadas dentro da janela
        AtomicInteger counter = new AtomicInteger(0);
    }

    /**
     * Mapa que armazena as janelas de rate limit
     * associadas a cada chave (usuário ou IP).
     */
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    /**
     * Construtor do filtro de rate limiting.
     *
     * @param requestsPerWindow número máximo de requisições permitidas
     * @param windowSeconds duração da janela de tempo em segundos
     */
    public RateLimitingFilter(int requestsPerWindow, int windowSeconds) {
        this.requestsPerWindow = requestsPerWindow;
        this.windowSeconds = windowSeconds;
    }

    /**
     * Método executado uma vez para cada requisição HTTP.
     * Aplica a lógica de rate limit antes de permitir
     * o processamento normal da requisição.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Resolve a chave de identificação (usuário autenticado ou IP)
        String key = resolveKey(request);

        // Obtém o timestamp atual em segundos
        long now = Instant.now().getEpochSecond();

        // Recupera ou cria a janela associada à chave
        Window w = windows.computeIfAbsent(key, k -> {
            Window nw = new Window();
            nw.windowStart = now;
            return nw;
        });

        // Sincroniza o acesso à janela para evitar problemas de concorrência
        synchronized (w) {

            // Caso a janela de tempo tenha expirado, reinicia o contador
            if (now - w.windowStart >= windowSeconds) {
                w.windowStart = now;
                w.counter.set(0);
            }

            // Incrementa o contador de requisições da janela atual
            int current = w.counter.incrementAndGet();

            // Verifica se o limite máximo de requisições foi excedido
            if (current > requestsPerWindow) {
                LOGGER.warn(
                    "Limite de requisições excedido para a chave {}: {}/{}",
                    key, current, requestsPerWindow
                );

                // Retorna HTTP 429 (Muitas requisições)
                response.setStatus(429);
                response.getWriter()
                        .write("Limite de requisições excedido. Tente novamente mais tarde.");
                return;
            }
        }

        // Continua o processamento normal da requisição
        filterChain.doFilter(request, response);
    }

    /**
     * Resolve a chave utilizada para controle de rate limit.
     *
     * Dá prioridade ao usuário autenticado; caso não exista,
     * utiliza o endereço IP da requisição.
     *
     * @param request requisição HTTP atual
     * @return chave identificadora para o rate limit
     */
    private String resolveKey(HttpServletRequest request) {

        // Obtém o usuário autenticado a partir do contexto de segurança
        var auth = org.springframework.security.core.context
                .SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return "user:" + auth.getName();
        }

        // Utiliza o endereço IP como fallback para usuários não autenticados
        String ip = request.getRemoteAddr();
        return "ip:" + ip;
    }
}
