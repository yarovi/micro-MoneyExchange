package org.inamsay.net.micromoneyexchange.client;

import org.inamsay.net.micromoneyexchange.dto.ExchangeRateResponseDTO;
import reactor.core.publisher.Mono;

public interface ExchangeRateProvider {
    Mono<ExchangeRateResponseDTO> getExchangeRate(String sourceCurrency, String targetCurrency, float amount);
}
