package com.washiner.currencysyncapi.integration.dto;

import java.util.Map;

// Esse record espelha EXATAMENTE o formato JSON que a API Frankfurter retorna:
// { "base": "USD", "date": "2026-06-24", "rates": { "BRL": 5.42 } }
//
// O Spring (via Jackson, a biblioteca de JSON por trás dos panos) vai
// converter o JSON da resposta automaticamente para esse objeto,
// casando os nomes dos campos.
public record FrankfurterResponse(
        String base,
        String date,
        Map<String, java.math.BigDecimal> rates
) {
}