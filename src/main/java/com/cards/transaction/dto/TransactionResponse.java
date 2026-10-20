
package com.cards.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    String referenceNumber,
    String merchantName,
    BigDecimal amount,
    String currency,
    String status,
    LocalDateTime createdAt
) {
}
