package com.washiner.currencysyncapi.controller;

import com.washiner.currencysyncapi.service.CurrencySyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Currency Sync", description = "Sincronização de cotações com a API externa (Frankfurter)")
@RestController
@RequestMapping("/v1/currencies/sync")
@RequiredArgsConstructor
public class CurrencySyncController {

    private final CurrencySyncService currencySyncService;

    @Operation(summary = "Dispara a sincronização manual das cotações de todas as moedas")
    @PostMapping
    public ResponseEntity<String> syncAll() {
        currencySyncService.syncAll();
        // 200 OK com uma mensagem simples -- essa operação não "cria" um
        // recurso novo (por isso não usamos 201), só executa uma ação.
        return ResponseEntity.ok("Sincronização concluída com sucesso!");
    }
}
