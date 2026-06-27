package com.washiner.currencysyncapi.service;

import com.washiner.currencysyncapi.domain.entity.Currency;
import com.washiner.currencysyncapi.dto.response.CurrencyUpdateNotification;
import com.washiner.currencysyncapi.integration.FrankfurterClient;
import com.washiner.currencysyncapi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencySyncService {

    private final CurrencyRepository currencyRepository;
    private final FrankfurterClient frankfurterClient;
    // Peça nova: o "microfone" que envia mensagens para os tópicos WebSocket.
    private final SimpMessagingTemplate messagingTemplate;

    private static final String REFERENCE_CURRENCY = "BRL";

    @Transactional
    public void syncAll() {
        List<Currency> currencies = currencyRepository.findAll();

        for (Currency currency : currencies) {
            if (currency.getCode().equals(REFERENCE_CURRENCY)) {
                continue;
            }

            try {
                BigDecimal newRate = frankfurterClient.getRateToBRL(currency.getCode());

                currency.setCurrentRate(newRate);
                currency.setUpdatedAt(LocalDateTime.now());

                currencyRepository.save(currency);

                log.info("Moeda {} sincronizada: nova cotação = {}", currency.getCode(), newRate);

                // PEÇA NOVA: depois de salvar com sucesso, "anunciamos" a
                // mudança no tópico /topic/currency-updates. Qualquer cliente
                // conectado nesse tópico recebe essa mensagem automaticamente.
                CurrencyUpdateNotification notification = new CurrencyUpdateNotification(
                        currency.getCode(),
                        currency.getCurrentRate(),
                        currency.getUpdatedAt()
                );
                messagingTemplate.convertAndSend("/topic/currency-updates", notification);

            } catch (Exception e) {
                log.error("Erro ao sincronizar a moeda {}: {}", currency.getCode(), e.getMessage());
            }
        }
    }
}