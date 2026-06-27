package com.washiner.currencysyncapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @EnableWebSocketMessageBroker: ativa o suporte a WebSocket + STOMP
// na aplicação. Sem essa anotação, nada de WebSocket funciona.
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Aqui registramos o "endereço" (endpoint) onde o cliente vai se
    // conectar para abrir a "ligação" WebSocket.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // O cliente vai se conectar em: ws://localhost:8080/ws-currency
                .addEndpoint("/ws-currency")
                // withSockJS(): adiciona um "plano B" para navegadores/clientes
                // que não suportam WebSocket nativo -- ele simula a mesma
                // experiência usando outras técnicas por trás dos panos.
                .withSockJS();
    }

    // Aqui configuramos o "broker" -- a peça que distribui as mensagens
    // para os tópicos certos.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // enableSimpleBroker: usa um broker SIMPLES, em memória, dentro da
        // própria aplicação (sem precisar instalar RabbitMQ, ActiveMQ, etc).
        // Perfeito para estudo -- em produção de alta escala, trocaria por
        // um broker externo dedicado.
        registry.enableSimpleBroker("/topic");
    }
}