
package com.cards.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, String>>
            handleTransactionNotFound(
                TransactionNotFoundException exception) {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "error", "TRANSACTION_NOT_FOUND",
                "message", exception.getMessage()
            ));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>>
            handleInvalidArgument(
                IllegalArgumentException exception) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error", "INVALID_REQUEST",
                "message", exception.getMessage()
            ));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String, String>>
            handleExternalApiException(
                ExternalApiException exception) {

        return ResponseEntity
            .status(exception.getStatus())
            .body(Map.of(
                "error", "EXTERNAL_API_ERROR",
                "message", exception.getMessage()
            ));
    }

}
