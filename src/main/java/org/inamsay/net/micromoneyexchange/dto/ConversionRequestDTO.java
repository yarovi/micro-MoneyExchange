package org.inamsay.net.micromoneyexchange.dto;

import lombok.Data;

@Data
public class ConversionRequestDTO {
    float amount;
    String sourceCurrency;
    String targetCurrency;
}
