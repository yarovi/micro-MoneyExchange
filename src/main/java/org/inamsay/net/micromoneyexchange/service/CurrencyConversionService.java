package org.inamsay.net.micromoneyexchange.service;

import org.inamsay.net.micromoneyexchange.dto.ConversionResponseDTO;
import org.inamsay.net.micromoneyexchange.dto.ConversionRequestDTO;
import reactor.core.publisher.Mono;

public interface CurrencyConversionService {
    Mono<ConversionResponseDTO> convert(ConversionRequestDTO request);
}
