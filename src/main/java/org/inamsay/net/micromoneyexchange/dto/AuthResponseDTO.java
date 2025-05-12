package org.inamsay.net.micromoneyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for authentication responses.
 * Contains the token field.
 */
@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
}
