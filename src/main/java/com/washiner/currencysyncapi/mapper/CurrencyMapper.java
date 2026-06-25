package com.washiner.currencysyncapi.mapper;

import com.washiner.currencysyncapi.domain.entity.Currency;
import com.washiner.currencysyncapi.dto.request.CurrencyRequest;
import com.washiner.currencysyncapi.dto.response.CurrencyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// componentModel = "spring": faz o MapStruct gerar a implementação como
// um @Component do Spring, para podermos injetar (@RequiredArgsConstructor)
// no Service como qualquer outro bean.
@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    // Converte do Request (o que o cliente envia) para a Entity (o que vai pro banco).
    // ignore = true nos campos abaixo porque eles NÃO vêm do cliente --
    // serão preenchidos manualmente pelo Service antes de salvar.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentRate", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Currency toEntity(CurrencyRequest request);

    // Converte da Entity (o que está no banco) para o Response (o que a API retorna).
    // Aqui não precisa de @Mapping porque os nomes dos campos são IDÊNTICOS
    // entre Currency e CurrencyResponse -- o MapStruct casa automaticamente.
    CurrencyResponse toResponse(Currency currency);
}