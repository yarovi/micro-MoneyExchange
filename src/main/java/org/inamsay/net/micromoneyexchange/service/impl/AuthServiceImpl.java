package org.inamsay.net.micromoneyexchange.service.impl;

import org.inamsay.net.micromoneyexchange.dto.AuthRequestDTO;
import org.inamsay.net.micromoneyexchange.dto.AuthResponseDTO;
import org.inamsay.net.micromoneyexchange.exception.AuthenticationFailedException;
import org.inamsay.net.micromoneyexchange.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl {
    private final ReactiveAuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(ReactiveAuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public Mono<AuthResponseDTO> authenticate(AuthRequestDTO request) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());

        return authManager.authenticate(auth)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Usuario no encontrado")))
                .map(authentication -> {
                    String role = authentication.getAuthorities().stream()
                            .findFirst() // Asumimos que solo hay un rol
                            .map(GrantedAuthority::getAuthority)
                            .orElse("ROLE_USER");
                    String token = jwtUtil.generateToken(authentication.getName(),role
                            );
                    return new AuthResponseDTO(token);
                })
                .onErrorResume(UsernameNotFoundException.class, e ->
                        Mono.error(new BadCredentialsException("Credenciales inválidas")))

                .onErrorResume(e ->
                        Mono.error(new AuthenticationFailedException("Error de autenticación", e)));
    }
}
