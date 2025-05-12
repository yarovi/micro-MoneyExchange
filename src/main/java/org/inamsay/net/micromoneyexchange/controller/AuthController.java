package org.inamsay.net.micromoneyexchange.controller;

import org.inamsay.net.micromoneyexchange.dto.AuthRequestDTO;
import org.inamsay.net.micromoneyexchange.dto.AuthResponseDTO;
import org.inamsay.net.micromoneyexchange.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * AuthController handles authentication requests.
 * It provides an endpoint for user login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    /**
     * Handles user login requests.
     *
     * @param request the authentication request containing username and password
     * @return a Mono containing the authentication response or an error response
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request) {
        return authService.authenticate(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
