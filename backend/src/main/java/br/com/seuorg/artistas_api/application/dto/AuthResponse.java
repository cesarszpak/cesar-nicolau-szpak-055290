package br.com.seuorg.artistas_api.application.dto;

/**
 * DTO responsável por representar a resposta de autenticação.
 * Contém o token de acesso e o refresh token.
 */
public class AuthResponse {

    /**
     * Token JWT utilizado para autenticar as requisições.
     */
    private String token;

    /**
     * Token utilizado para renovar o token de acesso.
     */
    private String refreshToken;

    /**
     * Construtor padrão.
     */
    public AuthResponse() {}

    /**
     * Construtor que inicializa os tokens de autenticação.
     *
     * @param token token JWT de acesso
     * @param refreshToken token para renovação do acesso
     */
    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    /**
     * Retorna o token de acesso.
     *
     * @return token JWT
     */
    public String getToken() {
        return token;
    }

    /**
     * Define o token de acesso.
     *
     * @param token token JWT
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Retorna o refresh token.
     *
     * @return token de renovação
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Define o refresh token.
     *
     * @param refreshToken token de renovação
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
