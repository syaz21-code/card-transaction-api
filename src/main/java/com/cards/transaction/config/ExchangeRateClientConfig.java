
package com.cards.transaction.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ExchangeRateClientConfig {

    @Bean
    public RestClient frankfurterRestClient(
            RestClient.Builder builder,
            @Value("${exchange-rate.api.base-url}") String baseUrl,
            @Value("${exchange-rate.api.connect-timeout}") Duration connectTimeout,
            @Value("${exchange-rate.api.read-timeout}") Duration readTimeout) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
