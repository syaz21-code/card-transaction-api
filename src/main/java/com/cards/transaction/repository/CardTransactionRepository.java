
package com.cards.transaction.repository;

import com.cards.transaction.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardTransactionRepository
        extends JpaRepository<CardTransaction, Long> {
}
