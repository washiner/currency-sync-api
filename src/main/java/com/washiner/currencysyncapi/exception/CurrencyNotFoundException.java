package com.washiner.currencysyncapi.exception;

// extends RuntimeException: assim não somos obrigados a declarar "throws"
// em todo método que possa lançar essa exceção -- ela "sobe" naturalmente
// até ser capturada pelo GlobalExceptionHandler.
public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(Long id) {
        super("Moeda não encontrada com o id: " + id);
    }
}