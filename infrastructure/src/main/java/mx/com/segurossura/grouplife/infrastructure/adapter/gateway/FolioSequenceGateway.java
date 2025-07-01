package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.FolioSequencePort;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.folio.FolioResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolioSequenceGateway implements FolioSequencePort {

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker folioSequenceCircuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry folioSequenceRetry;

    @Qualifier("folioSequenceWebClient")
    private final WebClient folioSequenceWebClient;

    @Override
    public Mono<Folio> getFolioSequence() {
        return this.folioSequenceWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/folios")
                        .queryParam("partnerId", "COTIZADORES_SURA")
                        .queryParam("solution", "VIDAGRUPO")
                        .build())
                .retrieve()
                .bodyToMono(FolioResponseDto.class)
                .doOnNext(folioResponseDto -> log.info("Response received from FolioSequence API: {}", folioResponseDto))
                .switchIfEmpty(Mono.error(new FolioException.FolioNotFound("Folio not found")))
                .flatMap(folioResponseDto -> {
                    final FolioResponseDto.FolioData data = folioResponseDto.getData();
                    log.info("Folio {}", data.getNumberFolio());
                    return Mono.just(Folio.builder()
                            .numberFolio(data.getNumberFolio())
                            .build());
                })
                .transformDeferred(RetryOperator.of(this.folioSequenceRetry))
                .transformDeferred(CircuitBreakerOperator.of(this.folioSequenceCircuitBreaker))
                .doOnError(e -> log.error("Error in FolioSequenceGateway: {}", e.getMessage()));
    }
}
