package org.inamsay.net.micromoneyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.inamsay.net.micromoneyexchange.exception.ErrorResponse;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
}
