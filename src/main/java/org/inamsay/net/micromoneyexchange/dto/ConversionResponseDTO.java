package org.inamsay.net.micromoneyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
