package br.com.seuorg.artistas_api.config;

import br.com.seuorg.artistas_api.domain.repository.UsuarioRepository;
import br.com.seuorg.artistas_api.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Classe de configuração de segurança da aplicação.
 *
 * Responsável por configurar autenticação, autorização,
 * CORS, política de sessão e o filtro JWT.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Repositório de usuários (usado pelo contexto de segurança)
    private final UsuarioRepository usuarioRepository;

    // Utilitário para manipulação e validação de tokens JWT
    private final JwtUtil jwtUtil;

    // URL do frontend autorizada a acessar a API (CORS)
    private final String frontendUrl;

    public SecurityConfig(
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil,
            @Value("${frontend.url:http://localhost:3000}") String frontendUrl
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Define o algoritmo de criptografia de senhas.
     *
     * @return PasswordEncoder baseado em BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração de CORS da aplicação.
     *
     * Define quais origens, métodos e cabeçalhos são permitidos
     * para requisições vindas do frontend.
     *
     * @return Fonte de configuração CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite requisições apenas da URL do frontend configurado
        configuration.setAllowedOrigins(List.of(frontendUrl));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeçalhos permitidos
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Permite envio de credenciais (cookies, headers de auth)
        configuration.setAllowCredentials(true);

        // Aplica a configuração para todas as rotas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Cadeia de filtros de segurança do Spring Security.
     *
     * Configura:
     * - Desativação de CSRF
     * - Política de sessão stateless
     * - Rotas públicas e protegidas
     * - Filtro de autenticação JWT
     *
     * @param http Configuração HTTP de segurança
     * @return SecurityFilterChain configurado
     * @throws Exception em caso de erro de configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Cria o filtro responsável por validar o JWT nas requisições
        var jwtFilter = new br.com.seuorg.artistas_api.security.JwtAuthenticationFilter(jwtUtil);

        http
            // Desabilita proteção CSRF (API stateless)
            .csrf().disable()

            // Habilita CORS com a configuração definida
            .cors(Customizer.withDefaults())

            // Define política de sessão como stateless (sem sessão no servidor)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configuração de autorização das rotas
            .authorizeHttpRequests(auth -> auth
                    // Endpoint público para leitura de capas
                    .requestMatchers(
                            org.springframework.http.HttpMethod.GET,
                            "/api/capas/*/conteudo"
                    ).permitAll()

                    // Endpoints públicos do Swagger/OpenAPI (UI e docs)
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/v3/api-docs.yaml",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/openapi.yaml",
                            "/v1/api-docs/**",
                            "/v1/api-docs"
                    ).permitAll()

                    // Endpoints públicos (login, refresh e criação/listagem básica)
                    .requestMatchers(
                            "/login",
                            "/refresh",
                            "/api/usuarios",
                            "/api/artistas"
                    ).permitAll()

                    // Qualquer outra rota exige autenticação
                    .anyRequest().authenticated()
            )

            // Adiciona o filtro JWT antes do filtro padrão de autenticação
            .addFilterBefore(
                    jwtFilter,
                    org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
            )

            // Desabilita autenticação HTTP Basic
            .httpBasic().disable()

            // Desabilita formulário de login padrão
            .formLogin().disable();

        return http.build();
    }
}
