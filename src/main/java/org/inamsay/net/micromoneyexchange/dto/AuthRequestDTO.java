package org.inamsay.net.micromoneyexchange.dto;

import lombok.Data;


/** * DTO for authentication requests.
 * Contains the username and password fields.
 */
@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}
