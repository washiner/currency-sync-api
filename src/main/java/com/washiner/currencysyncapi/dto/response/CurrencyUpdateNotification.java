package com.washiner.currencysyncapi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Esse record representa a mensagem que será ENVIADA via WebSocket
// para todos os clientes conectados, sempre que uma cotação mudar.
public record CurrencyUpdateNotification(
        String code,
        BigDecimal newRate,
        LocalDateTime updatedAt
) {
}
