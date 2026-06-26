package com.washiner.currencysyncapi.integration;

import com.washiner.currencysyncapi.integration.dto.FrankfurterResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

// @Component: classe simples gerenciada pelo Spring, sem ser
// Controller/Service/Repository -- ela só tem UMA responsabilidade:
// conversar com a API externa Frankfurter.
@Component
public class FrankfurterClient {

    // WebClient.create(...) cria um cliente HTTP já configurado com a
    // URL base da API. Toda chamada feita por esse client vai usar
    // essa URL como ponto de partida.
    private final WebClient webClient = WebClient.create("https://api.frankfurter.dev");

    // Busca a cotação de 1 unidade da moeda informada, em relação ao BRL.
    // Exemplo: getRateToBRL("USD") -> retorna quanto vale 1 USD em BRL.
    public BigDecimal getRateToBRL(String currencyCode) {

        FrankfurterResponse response = webClient.get()
                // .uri(...) monta a URL final juntando o path e os query params.
                // Resultado: /v1/latest?base=USD&symbols=BRL
                .uri("/v1/latest?base={base}&symbols=BRL", currencyCode)
                .retrieve()
                // .bodyToMono(...) diz ao WebClient: "quando a resposta chegar,
                // converta o JSON para um objeto FrankfurterResponse".
                .bodyToMono(FrankfurterResponse.class)
                // .block() força a chamada a esperar a resposta antes de continuar
                // (chamada síncrona/bloqueante -- mais simples para você aprender
                // agora; WebClient também suporta modo assíncrono/reativo, que é
                // assunto mais avançado para depois).
                .block();

        // Pega o valor de BRL dentro do mapa "rates" da resposta.
        return response.rates().get("BRL");
    }
}