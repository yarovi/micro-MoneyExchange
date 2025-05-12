package org.inamsay.net.micromoneyexchange.config;

import org.inamsay.net.micromoneyexchange.repository.UserRepository;
import org.inamsay.net.micromoneyexchange.security.JwtAuthenticationWebFilter;
import org.inamsay.net.micromoneyexchange.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for the exchange service.
 * This class configures the security settings for the application, including
 * authentication and authorization.
 */
@Configuration
public class ExchangeSecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationWebFilter jwtFilter;
    public ExchangeSecurityConfig(JwtUtil jwtUtil
         ,
         JwtAuthenticationWebFilter jwtFilter ){
        //                          ) {
        this.jwtUtil = jwtUtil;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the ServerHttpSecurity object to configure
     * @return the configured SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * Configures the ReactiveUserDetailsService for user authentication.
     *
     * @param userRepository the UserRepository to use for user details
     * @return the configured ReactiveUserDetailsService
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRole())
                        .build());
    }

    /**
     * Configures the ReactiveAuthenticationManager for user authentication.
     *
     * @param userDetailsService the ReactiveUserDetailsService to use for user details
     * @param passwordEncoder    the PasswordEncoder to use for password encoding
     * @return the configured ReactiveAuthenticationManager
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        var authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    /**
     * Configures the PasswordEncoder for password encoding.
     *
     * @return the configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
