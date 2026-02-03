package br.com.seuorg.artistas_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI (Swagger).
 *
 * Esta classe é responsável por definir as informações da documentação
 * da API, como título, versão, descrição, contato, licença e também
 * a configuração de segurança usando JWT (Bearer Token).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define a configuração personalizada do OpenAPI.
     *
     * @return objeto OpenAPI configurado com informações gerais,
     *         esquema de segurança e tags da aplicação
     */
    @Bean
    public OpenAPI customOpenAPI() {

        // Nome do esquema de segurança utilizado (Bearer JWT)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Informações básicas da API
                .info(new Info()
                        .title("Artistas API") // Título da API
                        .version("0.0.1") // Versão da API
                        .description("Documentação OpenAPI da API de artistas e álbuns") // Descrição geral
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento") // Nome do contato
                                .email("dev@example.com")) // Email de contato
                        .license(new License()
                                .name("MIT")) // Licença do projeto
                )

                // Configuração dos componentes do OpenAPI
                .components(new io.swagger.v3.oas.models.Components()
                        // Define o esquema de segurança JWT (Bearer)
                        .addSecuritySchemes(securitySchemeName,
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )

                // Aplica o esquema de segurança globalmente na API
                .addSecurityItem(
                        new io.swagger.v3.oas.models.security.SecurityRequirement()
                                .addList(securitySchemeName)
                )

                // Definição das tags utilizadas para organizar os endpoints no Swagger
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name("Artistas")
                        .description("Operações de artistas"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name("Álbuns")
                        .description("Operações de álbuns"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name("Capas")
                        .description("Gerenciamento de capas de álbuns"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name("Usuários")
                        .description("Cadastro e autenticação de usuários"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name("Health")
                        .description("Endpoints públicos para healthcheck"));
    }
}
