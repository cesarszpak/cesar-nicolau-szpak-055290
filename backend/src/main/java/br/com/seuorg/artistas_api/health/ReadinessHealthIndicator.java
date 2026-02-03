package br.com.seuorg.artistas_api.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HealthIndicator customizado para o grupo "readiness".
 *
 * Verifica conectividade com o banco de dados (executando SELECT 1)
 * e, opcionalmente, se o endpoint S3/MinIO está acessível (HEAD request).
 *
 * A checagem de S3 é controlada pela propriedade `s3.readiness-check-enabled`
 * (padrão: false) para evitar falhas em ambientes de teste sem MinIO.
 */
@Component
public class ReadinessHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final boolean s3CheckEnabled;
    private final String s3Endpoint;

    public ReadinessHealthIndicator(JdbcTemplate jdbcTemplate,
                                    @Value("${s3.readiness-check-enabled:false}") boolean s3CheckEnabled,
                                    @Value("${s3.endpoint:}") String s3Endpoint) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3CheckEnabled = s3CheckEnabled;
        this.s3Endpoint = s3Endpoint;
    }

    @Override
    public Health health() {
        // Verifica o DB
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception e) {
            return Health.down().withDetail("database", e.getMessage()).build();
        }

        // Verifica S3/MinIO apenas se habilitado explicitamente
        if (s3CheckEnabled && s3Endpoint != null && !s3Endpoint.isBlank()) {
            try {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(800))
                        .build();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(s3Endpoint))
                        .method("HEAD", HttpRequest.BodyPublishers.noBody())
                        .timeout(Duration.ofMillis(800))
                        .build();
                HttpResponse<Void> resp = client.send(req, HttpResponse.BodyHandlers.discarding());
                if (resp.statusCode() >= 500) {
                    return Health.down().withDetail("s3", "status=" + resp.statusCode()).build();
                }
            } catch (Exception e) {
                return Health.down().withDetail("s3", e.getMessage()).build();
            }
        }

        return Health.up().build();
    }
}
