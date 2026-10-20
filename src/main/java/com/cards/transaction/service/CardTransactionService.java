
package com.cards.transaction.service;

import com.cards.transaction.dto.CreateTransactionRequest;
import com.cards.transaction.dto.TransactionResponse;
import com.cards.transaction.dto.UpdateTransactionRequest;
import com.cards.transaction.dto.TransactionPageResponse;

import com.cards.transaction.entity.CardTransaction;
import com.cards.transaction.repository.CardTransactionRepository;
import com.cards.transaction.exception.TransactionNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    
    @Transactional
    public TransactionResponse updateTransaction(
            Long id,
            UpdateTransactionRequest request) {

        CardTransaction transaction = repository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id));

        transaction.setMerchantName(request.merchantName());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setStatus(request.status());

        CardTransaction updated = repository.save(transaction);

        return new TransactionResponse(
            updated.getId(),
            updated.getReferenceNumber(),
            updated.getMerchantName(),
            updated.getAmount(),
            updated.getCurrency(),
            updated.getStatus(),
            updated.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public TransactionPageResponse getAllTransactions(int page) {

        Pageable pageable = PageRequest.of(
            page,
            10,
            Sort.by(Sort.Direction.ASC, "id")
        );

        Page<CardTransaction> transactionPage =
            repository.findAll(pageable);

        Page<TransactionResponse> responsePage =
            transactionPage.map(transaction ->
                new TransactionResponse(
                    transaction.getId(),
                    transaction.getReferenceNumber(),
                    transaction.getMerchantName(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    transaction.getStatus(),
                    transaction.getCreatedAt()
                )
            );

        return new TransactionPageResponse(
            responsePage.getContent(),
            responsePage.getNumber(),
            responsePage.getSize(),
            responsePage.getTotalElements(),
            responsePage.getTotalPages(),
            responsePage.isFirst(),
            responsePage.isLast()
        );
    }


}
