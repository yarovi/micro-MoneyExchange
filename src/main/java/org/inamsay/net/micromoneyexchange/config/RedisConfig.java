package org.inamsay.net.micromoneyexchange.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inamsay.net.micromoneyexchange.dto.ExchangeRateResponseDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Redis.
 * This class configures the Redis template to use a custom serializer for the value type.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a ReactiveRedisTemplate bean.
     *
     * @param connectionFactory the ReactiveRedisConnectionFactory
     * @return a ReactiveRedisTemplate for String keys and ExchangeRateResponseDTO values
     */
    @Bean
    ReactiveRedisTemplate<String, ExchangeRateResponseDTO> redisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Jackson2JsonRedisSerializer<ExchangeRateResponseDTO> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper,ExchangeRateResponseDTO.class);

        RedisSerializationContext<String, ExchangeRateResponseDTO> serializationContext =
                RedisSerializationContext.<String, ExchangeRateResponseDTO>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
