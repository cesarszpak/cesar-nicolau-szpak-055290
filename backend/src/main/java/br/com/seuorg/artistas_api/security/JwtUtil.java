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

@Component
public class JwtUtil {

    private final SecretKey key;
    private final int expirationMinutes;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-minutes:5}") int expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(Usuario usuario) {
        ZonedDateTime now = ZonedDateTime.now();
        Date issuedAt = Date.from(now.toInstant());
        Date exp = Date.from(now.plusMinutes(expirationMinutes).toInstant());

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("nome", usuario.getNome())
                .setIssuedAt(issuedAt)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Date getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
    }
}
