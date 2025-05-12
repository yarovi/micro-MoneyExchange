package org.inamsay.net.micromoneyexchange.service.impl;

import org.inamsay.net.micromoneyexchange.client.ExchangeRateProvider;
import org.inamsay.net.micromoneyexchange.client.ExchangeRateProviderClient;
import org.inamsay.net.micromoneyexchange.dto.ConversionRequestDTO;
import org.inamsay.net.micromoneyexchange.dto.ConversionResponseDTO;
import org.inamsay.net.micromoneyexchange.service.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/** * Implementation of the CurrencyConversionService interface.
 * This service handles currency conversion requests by interacting with an external exchange rate provider.
 * It uses the ExchangeRateProviderClient to fetch the exchange rates and perform the conversion.
 */
@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    Logger logger = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);

    private final ExchangeRateProvider rateProvider;

    /** * Constructor for CurrencyConversionServiceImpl.
     * This constructor initializes the service with the provided ExchangeRateProviderClient.
     *
     * @param rateProviderClient The ExchangeRateProviderClient to be used for fetching exchange rates.
     */
    public CurrencyConversionServiceImpl(ExchangeRateProviderClient rateProviderClient) {
        this.rateProvider = rateProviderClient;
        logger.info("CurrencyConversionServiceImpl initialized");
    }

    /**
     * Converts the given currency amount from source currency to target currency.
     * This method interacts with the ExchangeRateProvider to fetch the exchange rate and perform the conversion.
     *
     * @param request The ConversionRequestDTO containing the source currency, target currency, and amount to be converted.
     * @return A Mono containing the ConversionResponseDTO with the conversion result.
     */
    @Override
    public Mono<ConversionResponseDTO> convert(ConversionRequestDTO request) {

        return rateProvider.
                getExchangeRate(request.getSourceCurrency(),
                        request.getTargetCurrency(),
                        request.getAmount())
                .map(rate -> {
                    return new ConversionResponseDTO(
                            request.getAmount(),
                            rate.getResult(),
                            request.getSourceCurrency(),
                            request.getTargetCurrency(),
                            rate.getInfo().getQuote()
                    );
                });
    }
}
