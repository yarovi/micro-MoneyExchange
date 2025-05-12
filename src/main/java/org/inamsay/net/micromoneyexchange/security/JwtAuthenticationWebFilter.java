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


/** * Web filter for JWT authentication.
 * This filter intercepts incoming requests and checks for a valid JWT token in the Authorization header.
 * If the token is valid, it extracts the username and role from the token and sets the authentication context.
 * If the token is invalid or expired, it returns an error response with the appropriate status code and message.
 */
@Component
public class JwtAuthenticationWebFilter  implements WebFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filters incoming requests to check for JWT authentication.
     * If a valid JWT token is found, it sets the authentication context.
     * If the token is invalid or expired, it returns an error response.
     *
     * @param exchange The server web exchange containing the request and response.
     * @param chain The web filter chain to continue processing the request.
     * @return A Mono that completes when the filter processing is done.
     */
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

    /**
     * Handles JWT errors by returning an error response with the appropriate status code and message.
     *
     * @param exchange The server web exchange containing the request and response.
     * @param status The HTTP status code to set in the response.
     * @param error The error type.
     * @param message The error message.
     * @param path The request path.
     * @return A Mono that completes when the error response is sent.
     */
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
