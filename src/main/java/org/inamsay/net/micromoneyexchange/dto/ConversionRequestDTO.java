package org.inamsay.net.micromoneyexchange.dto;

import lombok.Data;

/**
 * DTO for conversion requests.
 * Contains the amount, source currency, and target currency fields.
 */
@Data
public class ConversionRequestDTO {
    float amount;
    String sourceCurrency;
    String targetCurrency;
}
