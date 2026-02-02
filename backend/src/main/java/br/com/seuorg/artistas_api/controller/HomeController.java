package br.com.seuorg.artistas_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por expor um endpoint simples
 * para verificação do funcionamento da API.
 */
@RestController
public class HomeController {

    /**
     * Endpoint raiz da aplicação.
     *
     * Utilizado como teste rápido para confirmar
     * que a API está no ar e respondendo corretamente.
     *
     * @return Mensagem indicando que a API está funcionando
     */
    @GetMapping("/")
    public String home() {
        return "API Artistas funcionando!";
    }
}
