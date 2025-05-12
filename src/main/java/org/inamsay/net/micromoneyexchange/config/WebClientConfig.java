package org.inamsay.net.micromoneyexchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient.
 * This class configures the WebClient to connect to the exchange rate API.
 */
@Configuration
public class WebClientConfig {

    @Value("${api.exchange-rate.url}")
    private String exchangeRateApiUrl;

    /**
     * Creates a WebClient bean.
     *
     * @return a WebClient configured with the exchange rate API URL
     */
    @Bean
    public WebClient exchangeRateWebClient() {
        return WebClient.builder()
                .baseUrl(exchangeRateApiUrl)
                .build();
    }
}
