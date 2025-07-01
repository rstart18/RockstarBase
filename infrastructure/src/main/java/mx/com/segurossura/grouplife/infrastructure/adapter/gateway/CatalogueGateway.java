package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.CataloguePort;
import mx.com.segurossura.grouplife.domain.model.catalogue.PeopleResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalogue.PeopleResponseDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueGateway implements CataloguePort {

    private static final String PATH = "/people";
    private static final String QUERY_TYPE = "tipoIdentificadorPersona";
    private static final String QUERY_RFC = "identificadorPersona";

    private final CatalogueMapper catalogueMapper;

    @Qualifier("catalogueWebClient")
    private final WebClient catalogueWebClient;

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry retry;

    @Override
    public Mono<PeopleResponse> getByRfc(final String rfc) {
        return this.catalogueWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH)
                        .queryParam(QUERY_TYPE, "1")
                        .queryParam(QUERY_RFC, rfc)
                        .build())
                .retrieve()
                .bodyToMono(PeopleResponseDto.class)
                .flatMap(peopleResponseDto -> {
                    if (peopleResponseDto == null) {
                        log.error("No catalogue found. Returning empty Mono.");
                        return Mono.error(new GatewayException("No catalogue found", "VG_PER_NOTFOUND"));
                    }

                    return Mono.just(this.catalogueMapper.responseToModel(peopleResponseDto));
                })
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .doOnError(e -> log.error("Error in CatalogueGateway : {}", e.getMessage()));
    }
}
