package br.com.seuorg.artistas_api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Filtro responsável por autenticar requisições utilizando JWT.
 *
 * Este filtro é executado uma vez por requisição e verifica se existe
 * um token JWT válido no header Authorization. Caso exista e seja válido,
 * o usuário é autenticado no contexto de segurança do Spring.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Logger para auxiliar no debug e auditoria de autenticação
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Utilitário responsável por validar e extrair informações do JWT
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Método executado para cada requisição HTTP.
     *
     * Aqui é feita a leitura do header Authorization, validação do token
     * e criação do contexto de autenticação do Spring Security.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Obtém o header Authorization da requisição
        String header = request.getHeader("Authorization");
        LOGGER.debug("Authorization header present: {}", header != null);

        // Verifica se o header existe e começa com "Bearer "
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {

            // Extrai o token removendo o prefixo "Bearer "
            String token = header.substring(7);

            // Valida o token JWT
            boolean valid = jwtUtil.validateToken(token);
            LOGGER.debug("Token valid: {}", valid);

            if (valid) {
                // Extrai o usuário (subject) do token
                String username = jwtUtil.getSubject(token);
                LOGGER.debug("Authenticated user from token: {}", username);

                // Cria um objeto de autenticação simples com role padrão USER
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("USER"))
                );

                // Registra o usuário autenticado no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Continua o fluxo normal da requisição
        filterChain.doFilter(request, response);
    }
}
