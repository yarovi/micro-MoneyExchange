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

/**
 * ExchangeRateProviderClient is a client that fetches exchange rates from an external API and caches the results.
 * It implements the ExchangeRateProvider interface.
 */
@Component
public class ExchangeRateProviderClient implements ExchangeRateProvider {

    Logger logger = LoggerFactory.getLogger(ExchangeRateProviderClient.class);

    @Value("${cache.ttl.minutes}")
    private long ttlMinutes;
    @Value("${api.exchange-rate.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, ExchangeRateResponseDTO > cache;

    /**
     * Constructor for ExchangeRateProviderClient.
     *
     * @param apiClient the WebClient used to make API calls
     * @param cache1    the ReactiveRedisTemplate used for caching
     */
    public ExchangeRateProviderClient(WebClient apiClient,  ReactiveRedisTemplate<String, ExchangeRateResponseDTO > cache1) {
        this.webClient = apiClient;
        this.cache = cache1;
        logger.info("ExchangeRateProviderClient initialized");
    }

    /**
     * Retrieves the exchange rate for a given source and target currency, and caches the result.
     *
     * @param source the source currency
     * @param target the target currency
     * @param amount the amount to convert
     * @return a Mono containing the ExchangeRateResponseDTO
     */
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

    /**
     * Generates a cache key based on the source and target currencies and the amount.
     *
     * @param from   the source currency
     * @param to     the target currency
     * @param amount the amount to convert
     * @return a string representing the cache key
     */
    private String generateKey(String from, String to, float amount) {
        return String.format("%s_%s_%.2f", from, to, amount);
    }
    /**
     * Caches the exchange rate response.
     *
     * @param key  the cache key
     * @param rate the exchange rate response
     * @return a Mono indicating whether the caching was successful
     */
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

    /**
     * Fetches the exchange rate from the external API.
     *
     * @param sourceCurrency the source currency
     * @param targetCurrency the target currency
     * @param amount        the amount to convert
     * @return a Mono containing the ExchangeRateResponseDTO
     */
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
                        new HandlerExchangeRateException("No se pudo recuperar el tipo de cambio de la Cliente", e)));
    }
}
