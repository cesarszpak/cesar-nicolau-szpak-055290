package br.com.seuorg.artistas_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springdoc.webmvc.api.OpenApiWebMvcResource;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de acesso público aos endpoints relacionados ao OpenAPI e Swagger UI.
 *
 * Estes testes garantem que a documentação da API esteja acessível
 * sem necessidade de autenticação JWT.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiPublicTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock do recurso OpenApiWebMvcResource para evitar problemas de
     * incompatibilidade de runtime do Springdoc durante os testes.
     */
    @MockBean
    private OpenApiWebMvcResource openApiResource;

    /**
     * Verifica se o endpoint /v3/api-docs está acessível sem autenticação.
     */
    @Test
    void v3ApiDocs_shouldBeAccessibleWithoutAuth() throws Exception {
        // Mocka a resposta do Springdoc retornando um JSON mínimo válido
        when(openApiResource.openapiJson(any(), any(), any()))
                .thenReturn("{\"info\":{\"title\":\"Artistas API\"}}".getBytes());

        // Realiza a chamada ao endpoint e valida o retorno
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Artistas API")));
    }

    /**
     * Verifica se a interface do Swagger UI está acessível publicamente.
     */
    @Test
    void swaggerUi_shouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                // Garante que o HTML contém referência ao arquivo OpenAPI
                .andExpect(content().string(containsString("/openapi.yaml")));
    }

    /**
     * Verifica se o arquivo openapi.yaml é servido corretamente como arquivo estático.
     */
    @Test
    void openapi_yaml_shouldBeServedAsStaticFile() throws Exception {
        mockMvc.perform(get("/openapi.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("openapi: 3.0.3")));
    }
}
