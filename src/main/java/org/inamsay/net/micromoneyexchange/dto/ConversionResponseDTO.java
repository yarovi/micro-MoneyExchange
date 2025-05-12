package org.inamsay.net.micromoneyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * DTO for conversion responses.
 * Contains the source amount, converted amount, source currency, target currency, and exchange rate fields.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResponseDTO {
    float sourceAmount;
    BigDecimal convertedAmount;
    String sourceCurrency;
    String targetCurrency;
    BigDecimal exchangeRate;
}
