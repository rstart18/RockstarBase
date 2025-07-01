package mx.com.segurossura.grouplife.infrastructure.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class CustomConfiguration {
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    @Primary
    @Bean
    public CircuitBreaker folioSequenceCircuitBreaker(final CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("folioSequenceCircuitBreaker");
    }

    @Bean
    public CircuitBreaker saveFolioRecordCircuitBreaker(final CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("saveFolioRecordCircuitBreaker");
    }

    @Bean
    public Retry saveFolioRecordRetry() {
        final RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .build();
        return Retry.of("saveFolioRecordRetry", config);
    }

    @Primary
    @Bean
    public Retry folioSequenceRetry() {
        final RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .build();
        return Retry.of("folioSequenceRetry", config);
    }

    @Bean
    public Retry mongoDbRetry() {
        final RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(3))
                .build();
        return Retry.of("mongoDbRetry", config);
    }
}
