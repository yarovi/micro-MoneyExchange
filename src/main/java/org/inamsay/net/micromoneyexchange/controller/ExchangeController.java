package org.inamsay.net.micromoneyexchange.controller;

import org.inamsay.net.micromoneyexchange.dto.ConversionRequestDTO;
import org.inamsay.net.micromoneyexchange.dto.ConversionResponseDTO;
import org.inamsay.net.micromoneyexchange.service.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


/** * ExchangeController handles currency conversion requests.
 * It provides an endpoint for converting currencies.
 */
@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    Logger logger = LoggerFactory.getLogger(ExchangeController.class);
    private final CurrencyConversionService currencyConversionService;

    /**
     * Constructor for ExchangeController.
     *
     * @param currencyConversionService the service for currency conversion
     */
    public ExchangeController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }
    /**
     * Handles currency conversion requests.
     *
     * @param conversionRequest the conversion request containing amount, source currency, and target currency
     * @return a Mono containing the conversion response
     */
    @PostMapping("/convert")
    public Mono<ConversionResponseDTO> convertCurrency(@RequestBody ConversionRequestDTO conversionRequest) {
        logger.info("Received conversion request: {}", conversionRequest);
        Mono<ConversionResponseDTO> response = currencyConversionService.convert(conversionRequest);
        String output = "Converted " + conversionRequest.getAmount() + " " + conversionRequest.getSourceCurrency() + " to " + conversionRequest.getTargetCurrency();
        return response;
    }
}
