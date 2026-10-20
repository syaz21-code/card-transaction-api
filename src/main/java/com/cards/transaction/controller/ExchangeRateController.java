
package com.cards.transaction.controller;

import com.cards.transaction.dto.ExchangeRateResponse;
import com.cards.transaction.service.ExchangeRateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/{base}/{quote}")
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @PathVariable String base,
            @PathVariable String quote) {

        String baseCurrency = base.toUpperCase(Locale.ROOT);
        String quoteCurrency = quote.toUpperCase(Locale.ROOT);

        if (!baseCurrency.matches("[A-Z]{3}") ||
            !quoteCurrency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                "Currency codes must contain three letters"
            );
        }

        ExchangeRateResponse response =
                service.getExchangeRate(baseCurrency, quoteCurrency);

        return ResponseEntity.ok(response);
    }
}
