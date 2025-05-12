package org.inamsay.net.micromoneyexchange.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.inamsay.net.micromoneyexchange.exception.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationWebFilter  implements WebFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String path = exchange.getRequest().getPath().value();

            try {
                Claims claims = jwtUtil.validateToken(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                var auth = new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(role))
                );
                var context = new SecurityContextImpl(auth);
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            } catch (ExpiredJwtException e) {
                return handleJwtError(exchange, 401, "Unauthorized", "Token JWT expirado", path);
            } catch (MalformedJwtException e) {
                return handleJwtError(exchange, 401, "Unauthorized", "Token JWT malformado", path);
            } catch (UnsupportedJwtException e) {
                return handleJwtError(exchange, 401, "Unauthorized", "Token JWT no soportado", path);
            } catch (IllegalArgumentException e) {
                return handleJwtError(exchange, 401, "Unauthorized", "Token JWT inválido o vacío", path);
            } catch (Exception e) {
                return handleJwtError(exchange, 500, "Internal Server Error", "Error de autenticación JWT", path);
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> handleJwtError(ServerWebExchange exchange, int status, String error, String message, String path) {
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(status, error, message, path);

        try {
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(new ObjectMapper().writeValueAsBytes(errorResponse))));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
