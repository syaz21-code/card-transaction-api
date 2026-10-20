
package com.cards.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(
    LocalDate date,
    String base,
    String quote,
    BigDecimal rate
) {
}
