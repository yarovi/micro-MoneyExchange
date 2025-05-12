package org.inamsay.net.micromoneyexchange;

import io.jsonwebtoken.Claims;
import org.inamsay.net.micromoneyexchange.config.ExchangeSecurityConfig;
import org.inamsay.net.micromoneyexchange.controller.ExchangeController;
import org.inamsay.net.micromoneyexchange.dto.ConversionRequestDTO;
import org.inamsay.net.micromoneyexchange.dto.ConversionResponseDTO;
import org.inamsay.net.micromoneyexchange.security.JwtAuthenticationWebFilter;
import org.inamsay.net.micromoneyexchange.security.JwtUtil;
import org.inamsay.net.micromoneyexchange.service.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@WebFluxTest(controllers = ExchangeController.class)
@AutoConfigureWebTestClient
@Import(ExchangeSecurityConfig.class)
class MicroMoneyExchangeApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CurrencyConversionService currencyConversionService;

    @MockitoBean
    private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private ReactiveAuthenticationManager authenticationManager;
    @MockitoBean
    private ReactiveUserDetailsService userDetailsService;

    ConversionRequestDTO request;
    ConversionResponseDTO response;
    private final String TEST_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ0ZXN0LXVzZXIiLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlhdCI6MTY0NzI4NzYwMCwiZXhwIjoxNjQ3MjkxMjAwfQ." +
            "dummySignature";


    @BeforeEach
    public void setup() {
        request = new ConversionRequestDTO();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(100.0f);

        response = new ConversionResponseDTO();
        response.setSourceCurrency("USD");
        response.setTargetCurrency("EUR");
        response.setExchangeRate(new BigDecimal("0.92"));
        response.setConvertedAmount(new BigDecimal("89.797"));

        Mockito.when(jwtAuthenticationWebFilter.filter(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> {
                    ServerWebExchange exchange = invocation.getArgument(0);
                    WebFilterChain chain = invocation.getArgument(1);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            "test-user",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );
                    SecurityContextImpl context = new SecurityContextImpl(auth);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                });



    }

    @Test
    void testConvertCurrency() {
        Mockito.when(currencyConversionService.convert(request))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/exchange/convert")
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ConversionResponseDTO.class)
                .consumeWith(response -> {
                    ConversionResponseDTO responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getSourceCurrency().equals("USD");
                    assert responseBody.getTargetCurrency().equals("EUR");
                    assert responseBody.getExchangeRate().compareTo(new BigDecimal("0.92")) == 0;
                    assert responseBody.getConvertedAmount().compareTo(new BigDecimal("89.797")) == 0;
                });
    }


}
