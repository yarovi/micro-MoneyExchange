package org.inamsay.net.micromoneyexchange.client;

import org.inamsay.net.micromoneyexchange.dto.ExchangeRateResponseDTO;
import reactor.core.publisher.Mono;
/**
 * Interface for providing exchange rate information.
 * This interface defines a method to retrieve the exchange rate between two currencies.
 */
public interface ExchangeRateProvider {
    Mono<ExchangeRateResponseDTO> getExchangeRate(String sourceCurrency, String targetCurrency, float amount);
}
