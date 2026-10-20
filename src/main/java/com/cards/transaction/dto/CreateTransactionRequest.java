
package com.cards.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTransactionRequest(

    @NotBlank(message = "Merchant name is required")
    @Size(max = 100, message = "Merchant name is too long")
    String merchantName,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 16, fraction = 2)
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    @Pattern(
        regexp = "^[A-Z]{3}$",
        message = "Currency must be a three-letter uppercase code"
    )
    String currency
) {
}
