package com.washiner.currencysyncapi.service;

import com.washiner.currencysyncapi.domain.entity.Currency;

import com.washiner.currencysyncapi.exception.CurrencyNotFoundException;
import com.washiner.currencysyncapi.mapper.CurrencyMapper;
import com.washiner.currencysyncapi.repository.CurrencyRepository;
import com.washiner.currencysyncapi.dto.request.CurrencyRequest;
import com.washiner.currencysyncapi.dto.response.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// @Service: avisa o Spring que essa classe contém regras de negócio
// e deve ser gerenciada como um Bean (igual @Component, mas com semântica clara).
@Service
// @RequiredArgsConstructor (Lombok): gera um construtor com todos os campos
// "final" abaixo. É assim que injetamos as dependências (Repository, Mapper)
// sem precisar escrever o construtor na mão.
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    // readOnly = true: avisa o Hibernate que essa transação só LÊ dados,
    // permitindo otimizações internas (não precisa rastrear mudanças pra commit).
    @Transactional(readOnly = true)
    public List<CurrencyResponse> findAll() {
        return currencyRepository.findAll()
                .stream()
                .map(currencyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CurrencyResponse findById(Long id) {
        Currency currency = findCurrencyOrThrow(id);
        return currencyMapper.toResponse(currency);
    }

    // Sem readOnly: essa transação ESCREVE no banco (insert).
    // Se algo falhar no meio do caminho, o Spring desfaz tudo (rollback).
    @Transactional
    public CurrencyResponse create(CurrencyRequest request) {
        Currency currency = currencyMapper.toEntity(request);

        // Regra de negócio: toda moeda nova começa com cotação ZERO
        // até que o serviço de sincronização externa a atualize de verdade.
        currency.setCurrentRate(BigDecimal.ZERO);
        currency.setUpdatedAt(LocalDateTime.now());

        Currency saved = currencyRepository.save(currency);
        return currencyMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Currency currency = findCurrencyOrThrow(id);
        currencyRepository.delete(currency);
    }

    // Método auxiliar privado: evita repetir a mesma lógica de
    // "buscar ou lançar exceção" em findById e delete.
    private Currency findCurrencyOrThrow(Long id) {
        return currencyRepository.findById(id)
                .orElseThrow(() -> new CurrencyNotFoundException(id));
    }
}