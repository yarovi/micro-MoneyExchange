package org.inamsay.net.micromoneyexchange.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for exchange rate responses.
 * Contains the success status, query details, info details, and result amount.
 */
@Data
public class ExchangeRateResponseDTO {
    private boolean success;
    private Query query;
    private Info info;
    private BigDecimal result;
    @Data
    public static class Query {
        private String from;
        private String to;
        private BigDecimal amount;
    }
    @Data
    public static class Info {
        private BigDecimal quote;
        private int timestamp;
    }
}
