package org.inamsay.net.micromoneyexchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.exchange-rate.url}")
    private String exchangeRateApiUrl;

    @Bean
    public WebClient exchangeRateWebClient() {
        return WebClient.builder()
                .baseUrl(exchangeRateApiUrl)
                .build();
    }
}
