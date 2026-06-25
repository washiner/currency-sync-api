package com.washiner.currencysyncapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// record: gera automaticamente construtor, getters, equals, hashCode, toString.
// Usamos record para DTOs porque eles são objetos "imutáveis" -- só carregam
// dado, não têm lógica, e não precisam ser alterados depois de criados.
public record CurrencyRequest(

        @NotBlank(message = "O código da moeda é obrigatório")
        @Size(min = 3, max = 3, message = "O código deve ter exatamente 3 letras (ex: USD)")
        String code,

        @NotBlank(message = "O nome da moeda é obrigatório")
        String name
) {
}