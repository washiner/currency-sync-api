package com.washiner.currencysyncapi.service;

import com.washiner.currencysyncapi.domain.entity.Currency;
import com.washiner.currencysyncapi.integration.FrankfurterClient;
import com.washiner.currencysyncapi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// @Slf4j (Lombok): gera automaticamente um objeto "log" para a classe,
// sem precisar declarar manualmente o Logger. Útil para registrar
// o que está acontecendo durante a sincronização (sucesso, erro, etc).
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencySyncService {

    private final CurrencyRepository currencyRepository;
    private final FrankfurterClient frankfurterClient;

    // O Real (BRL) é a nossa moeda de referência -- nunca sincronizamos
    // ela com ela mesma (1 BRL = 1 BRL, não faz sentido chamar a API pra isso).
    private static final String REFERENCE_CURRENCY = "BRL";

    @Transactional
    public void syncAll() {
        List<Currency> currencies = currencyRepository.findAll();

        for (Currency currency : currencies) {
            // Pula o BRL -- ele é a referência, não precisa buscar cotação dele mesmo.
            if (currency.getCode().equals(REFERENCE_CURRENCY)) {
                continue;
            }

            try {
                BigDecimal newRate = frankfurterClient.getRateToBRL(currency.getCode());

                currency.setCurrentRate(newRate);
                currency.setUpdatedAt(LocalDateTime.now());

                currencyRepository.save(currency);

                log.info("Moeda {} sincronizada: nova cotação = {}", currency.getCode(), newRate);

            } catch (Exception e) {
                // Se UMA moeda falhar (ex: API externa fora do ar), registramos
                // o erro mas continuamos tentando as outras -- uma falha não
                // deve travar a sincronização inteira.
                log.error("Erro ao sincronizar a moeda {}: {}", currency.getCode(), e.getMessage());
            }
        }
    }
}
