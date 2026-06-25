package com.washiner.currencysyncapi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyResponse(
        Long id,
        String code,
        String name,
        BigDecimal currentRate,
        LocalDateTime updatedAt
) {
}
