package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components()
                .addSecuritySchemes(
                        BEARER_AUTH,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                .addResponses("EntradaInvalida", problem("Entrada inválida", 400))
                .addResponses("NaoAutenticado", problem("Token ausente, inválido ou credenciais incorretas", 401))
                .addResponses("SemPermissao", problem("Perfil sem permissão para esta operação", 403))
                .addResponses("NaoEncontrado", problem("Usuário ou acesso não encontrado", 404))
                .addResponses("Conflito", problem("Conflito com o estado atual do recurso", 409));

        return new OpenAPI()
                .info(new Info()
                        .title("Temporary Access Manager API")
                        .description("API para cadastro de usuários e gerenciamento de acessos temporários.")
                        .version("v1"))
                .components(components);
    }

    private io.swagger.v3.oas.models.responses.ApiResponse problem(String description, int status) {
        Example example = new Example()
                .value(Map.of(
                        "type",
                        "https://example.com/errors/exemplo",
                        "title",
                        description,
                        "status",
                        status,
                        "detail",
                        description + ".",
                        "instance",
                        "/api/exemplo"));
        return new io.swagger.v3.oas.models.responses.ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/problem+json", new MediaType().addExamples("exemplo", example)));
    }
}
