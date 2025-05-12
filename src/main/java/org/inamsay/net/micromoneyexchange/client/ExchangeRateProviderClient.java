package org.inamsay.net.micromoneyexchange.client;

import org.inamsay.net.micromoneyexchange.dto.ExchangeRateResponseDTO;
import org.inamsay.net.micromoneyexchange.exception.HandlerExchangeRateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ExchangeRateProviderClient implements ExchangeRateProvider {

    Logger logger = LoggerFactory.getLogger(ExchangeRateProviderClient.class);

    @Value("${cache.ttl.minutes}")
    private long ttlMinutes;
    @Value("${api.exchange-rate.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, ExchangeRateResponseDTO > cache;

    public ExchangeRateProviderClient(WebClient apiClient,  ReactiveRedisTemplate<String, ExchangeRateResponseDTO > cache1) {
        this.webClient = apiClient;
        this.cache = cache1;
        logger.info("ExchangeRateProviderClient initialized");
    }

    public Mono<ExchangeRateResponseDTO> getExchangeRate(String source, String target, float amount) {
        String key = generateKey(source, target, amount);
        return cache.opsForValue()
                .get(key)
                .flatMap(cachedRate -> {
                    logger.info("Valor recuperado de la caché: " + cachedRate);
                    return Mono.just(cachedRate);
                })
                .switchIfEmpty(fetchExchangeRateFromApi(source, target, amount)
                        .doOnNext(rate -> logger.info("Fetched from API: {}", rate))
                        .flatMap(rate -> cacheRate(key, rate).thenReturn(rate)));

    }

    private String generateKey(String from, String to, float amount) {
        return String.format("%s_%s_%.2f", from, to, amount);
    }
    private Mono<Boolean> cacheRate(String key, ExchangeRateResponseDTO  rate) {
        Duration timeout = Duration.ofMinutes(ttlMinutes);
        return cache.opsForValue()
                .set(key, rate,timeout)
                .doOnSuccess(aBoolean -> {
                    if (aBoolean) {
                        logger.info("Valor almacenado en caché: " + rate);
                    } else {
                        logger.error("Error al almacenar el valor en caché");
                    }
                })
                .then(Mono.just(true));
    }
    private Mono<ExchangeRateResponseDTO> fetchExchangeRateFromApi(String sourceCurrency, String targetCurrency, float amount) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/convert")
                        .queryParam("from", sourceCurrency)
                        .queryParam("to", targetCurrency)
                        .queryParam("amount", amount)
                        .queryParam("access_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateResponseDTO.class)

                .onErrorResume(e -> Mono.error(
                        new HandlerExchangeRateException("Failed to fetch exchange rate of Client", e)));
    }
}
