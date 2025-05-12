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

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    Logger logger = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);

    private final ExchangeRateProvider rateProvider;

    public CurrencyConversionServiceImpl(ExchangeRateProviderClient rateProviderClient) {
        this.rateProvider = rateProviderClient;
        logger.info("CurrencyConversionServiceImpl initialized");
    }

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
