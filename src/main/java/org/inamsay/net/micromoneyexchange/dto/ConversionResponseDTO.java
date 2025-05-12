package org.inamsay.net.micromoneyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ConversionResponseDTO {
    float sourceAmount;
    BigDecimal convertedAmount;
    String sourceCurrency;
    String targetCurrency;
    BigDecimal exchangeRate;
}
