package com.washiner.currencysyncapi.controller;

import com.washiner.currencysyncapi.service.CurrencyService;
import com.washiner.currencysyncapi.dto.request.CurrencyRequest;
import com.washiner.currencysyncapi.dto.response.CurrencyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @Tag: agrupa esses endpoints sob um título no Swagger UI.
@Tag(name = "Currencies", description = "Gerenciamento de moedas acompanhadas")
// /v1/: versionamento de endpoint, igual você já faz em outros projetos.
@RestController
@RequestMapping("/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(summary = "Lista todas as moedas cadastradas")
    @GetMapping
    public ResponseEntity<List<CurrencyResponse>> findAll() {
        return ResponseEntity.ok(currencyService.findAll());
    }

    @Operation(summary = "Busca uma moeda pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<CurrencyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.findById(id));
    }

    @Operation(summary = "Cadastra uma nova moeda (cotação inicial = 0, atualizada pela sincronização)")
    @PostMapping
    public ResponseEntity<CurrencyResponse> create(@Valid @RequestBody CurrencyRequest request) {
        CurrencyResponse response = currencyService.create(request);
        // 201 Created: convenção HTTP para "algo novo foi criado com sucesso".
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Remove uma moeda")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        currencyService.delete(id);
        // 204 No Content: sucesso, mas sem corpo de resposta (deletar não retorna nada).
        return ResponseEntity.noContent().build();
    }
}
