
package com.cards.transaction.service;

import com.cards.transaction.dto.ExchangeRateResponse;
import com.cards.transaction.exception.ExternalApiException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.Locale;

@Service
public class ExchangeRateService {

    private final RestClient restClient;

    public ExchangeRateService(
            @Qualifier("frankfurterRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public ExchangeRateResponse getExchangeRate(
            String base,
            String quote) {

        try {
            ExchangeRateResponse response = restClient
                    .get()
                    .uri("/v2/rate/{base}/{quote}", base, quote)
                    .retrieve()
                    .body(ExchangeRateResponse.class);

            if (response == null ||
                response.date() == null ||
                response.base() == null ||
                response.quote() == null ||
                response.rate() == null ||
                response.rate().signum() <= 0 ||
                !base.equals(response.base().toUpperCase(Locale.ROOT)) ||
                !quote.equals(response.quote().toUpperCase(Locale.ROOT))) {

                throw new ExternalApiException(
                    HttpStatus.BAD_GATEWAY,
                    "Exchange-rate provider returned invalid data"
                );
            }

            return response;

        } catch (ExternalApiException exception) {
            throw exception;

        } catch (RestClientResponseException exception) {

            int status = exception.getStatusCode().value();

            if (status == 400 || status == 404 || status == 422) {
                throw new ExternalApiException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported currency or exchange-rate pair"
                );
            }

            throw new ExternalApiException(
                HttpStatus.BAD_GATEWAY,
                "Exchange-rate provider returned an error"
            );

        } catch (ResourceAccessException exception) {

            Throwable cause = exception;
            boolean timeout = false;

            while (cause != null) {
                if (cause instanceof SocketTimeoutException) {
                    timeout = true;
                    break;
                }
                cause = cause.getCause();
            }

            throw new ExternalApiException(
                timeout
                    ? HttpStatus.GATEWAY_TIMEOUT
                    : HttpStatus.BAD_GATEWAY,
                timeout
                    ? "Exchange-rate provider timed out"
                    : "Exchange-rate provider is unavailable"
            );

        } catch (RestClientException exception) {
            throw new ExternalApiException(
                HttpStatus.BAD_GATEWAY,
                "Unable to process exchange-rate response"
            );
        }
    }
}
