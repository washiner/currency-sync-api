package com.washiner.currencysyncapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration: avisa o Spring que esta classe contém configurações
// que devem ser carregadas na inicialização da aplicação.
@Configuration
public class SwaggerConfig {

    // @Bean: diz ao Spring "gerencie esse objeto para mim".
    // O Spring vai chamar esse método uma vez, guardar o resultado,
    // e usar esse OpenAPI automaticamente para montar o Swagger UI.
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Currency Sync API")
                        .version("v1")
                        .description("API de sincronização de cotações de moedas " +
                                "(BRL, USD, EUR, JPY) com notificações em tempo real via WebSocket " +
                                "e proteção por Rate Limiting.")
                        .contact(new Contact()
                                .name("Washiner")
                                .email("washiner@gmail.com")));
    }
}
