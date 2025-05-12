package org.inamsay.net.micromoneyexchange.service;

import org.inamsay.net.micromoneyexchange.dto.ConversionResponseDTO;
import org.inamsay.net.micromoneyexchange.dto.ConversionRequestDTO;
import reactor.core.publisher.Mono;


/** * Service interface for currency conversion operations.
 * This interface defines a method to convert currencies based on the provided request.
 */
public interface CurrencyConversionService {
    Mono<ConversionResponseDTO> convert(ConversionRequestDTO request);
}
