
package com.cards.transaction.controller;

import com.cards.transaction.dto.CreateTransactionRequest;
import com.cards.transaction.dto.TransactionResponse;
import com.cards.transaction.service.CardTransactionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class CardTransactionController {

    private final CardTransactionService service;

    public CardTransactionController(
            CardTransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        TransactionResponse response =
            service.createTransaction(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id) {

        TransactionResponse response =
            service.getTransactionById(id);

        return ResponseEntity.ok(response);
    }

}
