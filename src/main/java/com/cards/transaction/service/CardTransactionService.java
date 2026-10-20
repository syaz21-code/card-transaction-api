
package com.cards.transaction.service;

import com.cards.transaction.dto.CreateTransactionRequest;
import com.cards.transaction.dto.TransactionResponse;
import com.cards.transaction.entity.CardTransaction;
import com.cards.transaction.repository.CardTransactionRepository;
import com.cards.transaction.exception.TransactionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CardTransactionService {

    private final CardTransactionRepository repository;

    public CardTransactionService(
            CardTransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TransactionResponse createTransaction(
            CreateTransactionRequest request) {

        CardTransaction transaction = new CardTransaction();

        transaction.setReferenceNumber(
            "TXN-" + UUID.randomUUID().toString()
        );

        transaction.setMerchantName(request.merchantName());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(LocalDateTime.now());

        CardTransaction saved = repository.save(transaction);

        return new TransactionResponse(
            saved.getId(),
            saved.getReferenceNumber(),
            saved.getMerchantName(),
            saved.getAmount(),
            saved.getCurrency(),
            saved.getStatus(),
            saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {

        CardTransaction transaction = repository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id));

        return new TransactionResponse(
            transaction.getId(),
            transaction.getReferenceNumber(),
            transaction.getMerchantName(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getStatus(),
            transaction.getCreatedAt()
        );
    }

}
