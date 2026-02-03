package br.com.seuorg.artistas_api.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Controller responsável por servir a interface do Swagger UI.
 *
 * Esta classe carrega manualmente o arquivo HTML do Swagger UI
 * a partir dos recursos estáticos da aplicação e o disponibiliza
 * no endpoint /swagger-ui/index.html.
 */
@Controller
public class SwaggerUiController {

    /**
     * Endpoint que retorna a página principal do Swagger UI.
     *
     * @return ResponseEntity contendo o HTML do Swagger UI ou
     *         uma mensagem de erro em caso de falha
     */
    @GetMapping("/swagger-ui/index.html")
    public ResponseEntity<String> swaggerUi() {
        try {
            // Carrega o arquivo index.html do Swagger UI a partir do classpath
            var resource = new ClassPathResource("static/swagger-ui-static/index.html");

            // Abre o InputStream para leitura do arquivo
            try (InputStream in = resource.getInputStream()) {

                // Converte o conteúdo do arquivo HTML para String
                String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

                // Define o header de resposta como HTML
                var headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);

                // Retorna o HTML com status 200 (OK)
                return new ResponseEntity<>(content, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            // Retorna erro interno caso não seja possível carregar o Swagger UI
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao carregar Swagger UI");
        }
    }
}
