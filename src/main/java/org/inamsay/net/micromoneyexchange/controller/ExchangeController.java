package org.inamsay.net.micromoneyexchange.controller;

import org.inamsay.net.micromoneyexchange.dto.ConversionRequestDTO;
import org.inamsay.net.micromoneyexchange.dto.ConversionResponseDTO;
import org.inamsay.net.micromoneyexchange.service.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    Logger logger = LoggerFactory.getLogger(ExchangeController.class);
    private final CurrencyConversionService currencyConversionService;

    public ExchangeController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }
    @PostMapping("/convert")
    public Mono<ConversionResponseDTO> convertCurrency(@RequestBody ConversionRequestDTO conversionRequest) {
        logger.info("Received conversion request: {}", conversionRequest);
        Mono<ConversionResponseDTO> response = currencyConversionService.convert(conversionRequest);
        String output = "Converted " + conversionRequest.getAmount() + " " + conversionRequest.getSourceCurrency() + " to " + conversionRequest.getTargetCurrency();
        return response;
    }
}
