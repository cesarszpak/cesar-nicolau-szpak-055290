package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.health.ReadinessHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por expor probes de Liveness e Readiness.
 *
 * Disponibiliza endpoints em /actuator/probes/* para verificação de saúde
 * da aplicação, mesmo quando o suporte nativo a probes não está habilitado.
 */
@RestController
@RequestMapping("/actuator/probes")
public class ActuatorProbesController {

    private final ReadinessHealthIndicator readinessIndicator;

    public ActuatorProbesController(ReadinessHealthIndicator readinessIndicator) {
        this.readinessIndicator = readinessIndicator;
    }

    /**
     * Probe de Liveness.
     *
     * Indica se a aplicação está viva (processo em execução).
     */
    @GetMapping("/liveness")
    public ResponseEntity<Object> liveness() {
        var body = java.util.Map.of("status", "UP");
        return ResponseEntity.ok(body);
    }

    /**
     * Probe de Readiness.
     *
     * Indica se a aplicação está pronta para receber tráfego.
     */
    @GetMapping("/readiness")
    public ResponseEntity<Object> readiness() {
        Health health = readinessIndicator.health();
        var status = health.getStatus();
        var body = java.util.Map.of("status", status.getCode());

        if (status.equals(org.springframework.boot.actuate.health.Status.UP)) {
            return ResponseEntity.ok(body);
        }

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(body);
    }
}
