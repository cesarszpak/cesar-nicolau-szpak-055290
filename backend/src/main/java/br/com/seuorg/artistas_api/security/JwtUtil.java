package br.com.seuorg.artistas_api.security;

import br.com.seuorg.artistas_api.domain.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Utilitário para geração, validação e manipulação de tokens JWT.
 * Responsável por criar tokens para autenticação e verificar validade dos mesmos.
 */
@Component
public class JwtUtil {

    /** Chave secreta usada para assinar o token JWT */
    private final SecretKey key;

    /** Tempo de expiração do token, em minutos */
    private final int expirationMinutes;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-minutes:5}") int expirationMinutes) {
        // Converte a string secreta em uma chave HMAC para assinatura do JWT
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * Gera um token JWT para o usuário fornecido.
     *
     * @param usuario usuário para quem o token será gerado
     * @return token JWT como string
     */
    public String generateToken(Usuario usuario) {
        ZonedDateTime now = ZonedDateTime.now();
        Date issuedAt = Date.from(now.toInstant());
        Date exp = Date.from(now.plusMinutes(expirationMinutes).toInstant());

        return Jwts.builder()
                .setSubject(usuario.getEmail()) // Define o "subject" do token como o email do usuário
                .claim("nome", usuario.getNome()) // Adiciona o nome como claim adicional
                .setIssuedAt(issuedAt) // Data de emissão do token
                .setExpiration(exp) // Data de expiração do token
                .signWith(key, SignatureAlgorithm.HS256) // Assina o token usando HMAC SHA256
                .compact();
    }

    /**
     * Valida se o token JWT é válido e foi assinado corretamente.
     *
     * @param token token JWT
     * @return true se válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtém o "subject" (usuário/email) do token JWT.
     *
     * @param token token JWT
     * @return email do usuário
     */
    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Obtém a data de expiração do token JWT.
     *
     * @param token token JWT
     * @return data de expiração
     */
    public Date getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
    }
}
