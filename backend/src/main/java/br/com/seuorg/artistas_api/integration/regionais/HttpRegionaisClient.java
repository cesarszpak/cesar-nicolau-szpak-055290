package br.com.seuorg.artistas_api.integration.regionais;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

/**
 * Cliente HTTP responsável por buscar as regionais em um serviço externo.
 * Utiliza o WebClient do Spring para realizar a requisição e converter a resposta em DTOs.
 */
@Component
public class HttpRegionaisClient implements RegionaisClient {

    private final WebClient webClient;
    private final String url;

    /**
     * Construtor que recebe a URL configurada no application.properties
     * e o builder do WebClient.
     */
    public HttpRegionaisClient(
            @Value("${geia.regionais.url:https://integrador-argus-api.geia.vip/v1/regionais}") String url,
            WebClient.Builder webClientBuilder
    ) {
        this.url = url;
        this.webClient = webClientBuilder.build();
    }

    /**
     * Busca todas as regionais no serviço externo.
     * Converte a resposta JSON em uma lista de ExternalRegionalDto.
     */
    @Override
    public List<ExternalRegionalDto> fetchAll() {
        ExternalRegionalDto[] arr = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ExternalRegionalDto[].class)
                .block();

        if (arr == null) return List.of();
        return Arrays.asList(arr);
    }
}
