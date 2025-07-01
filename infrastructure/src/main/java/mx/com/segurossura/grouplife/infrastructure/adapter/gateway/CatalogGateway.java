package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.coverage.ExecutiveInfoDataResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog.CatalogListDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog.CoveragePlanDataGtwDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageCatalogDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.ExecutiveInfoDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogGateway implements CatalogPort {
    private static final String CATALOGS = "/catalogs/902";
    private static final String CATALOG = "catalog";
    private static final String COVERAGE = "COVERAGE";

    private final CatalogMapper catalogMapper;

    @Qualifier("catalogWebClient")
    private final WebClient catalogWebClient;

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry retry;

    @Override
    public Flux<CatalogItem> getBusinessActivity() {
        return this.catalogWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CATALOGS)
                        .queryParam(CATALOG, "GIROS")
                        .build())
                .retrieve()
                .bodyToMono(CatalogListDto.class)
                .flatMapMany(response -> {
                    if (response == null || response.data() == null) {
                        log.error("No business activities found. Terminando la aplicación.");
                        return Mono.error(new GatewayException("No business activities found.", "VG-GTW-NO-ACTIVITIES"));
                    }
                    return Flux.fromIterable(response.data());
                })
                .map(this.catalogMapper::toModel)
                .doOnError(e -> log.error("Error in CatalogGateway | GIROS: {}", e.getMessage()));
    }

    @Override
    public Mono<List<CoverageCatalog>> getCoverages() {
        return this.catalogWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CATALOGS)
                        .queryParam(CATALOG, COVERAGE)
                        .build())
                .retrieve()
                .bodyToMono(InsuranceDataDto.class)
                .flatMap(insuranceData -> {
                    if (insuranceData.data() == null || insuranceData.data().isEmpty()) {
                        log.error("No coverages found. Returning empty Mono.");
                        return Mono.error(new GatewayException("No coverages found", "VG-GTW-NO-COVERAGES"));
                    }
                    final List<CoverageCatalogDto> coveragesDto = insuranceData.data().getFirst().coverages();
                    final List<CoverageCatalog> coverages = this.catalogMapper.toModelList(coveragesDto);

                    return Mono.just(coverages);
                })
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .doOnError(e -> log.error("Error in CatalogGateway | COVERAGE: {}", e.getMessage()));
    }

    @Override
    public Mono<ExecutiveInfoDataResponse.ExecutiveInfo> getExecutiveName(final String insuranceAgentKey, final String subgroupKey) {
        return this.catalogWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CATALOGS + "/tariff-profile")
                        .queryParam("insuranceAgentKey", insuranceAgentKey)
                        .queryParam("subgroupKey", subgroupKey)
                        .build())
                .retrieve()
                .bodyToMono(ExecutiveInfoDataResponseDto.class)
                .flatMap(executiveInfoDataReqDto -> {
                    if (executiveInfoDataReqDto.data() == null || executiveInfoDataReqDto.data().isEmpty()) {
                        log.error("No Executive found. Returning empty Mono.");
                        return Mono.error(new GatewayException("No Executive found", "VG-GTW-NO-EXECUTIVE"));
                    }
                    final ExecutiveInfoDataResponseDto.ExecutiveInfo executiveInfoDto = executiveInfoDataReqDto.data().getFirst();
                    final ExecutiveInfoDataResponse.ExecutiveInfo executiveInfo = this.catalogMapper.executiveDtoToModel(executiveInfoDto);

                    return Mono.just(executiveInfo);
                })
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .doOnError(e -> log.error("Error in CatalogGateway | getExecutiveName: {}", e.getMessage()));
    }

    @Override
    public Flux<CoveragePlanModality> getCoveragePlan() {
        return this.catalogWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CATALOGS)
                        .queryParam(CATALOG, COVERAGE)
                        .queryParam("key", "PLANS_COVERAGE")
                        .build())
                .retrieve()
                .bodyToMono(CoveragePlanDataGtwDto.class)
                .flatMapMany(response -> {
                    if (response == null || response.data() == null) {
                        log.error("No coverages plans found. Terminando la aplicación.");
                        return Mono.error(new GatewayException("No coverages plans found.", "VG-GTW-NO-COVERAGEPLANS"));
                    }
                    return Flux.fromIterable(response.data().getFirst().coveragePlans());
                })
                .map(this.catalogMapper::toModel)
                .doOnError(e -> log.error("Error in CatalogGateway | COVERAGE|PLAN: {}", e.getMessage()));
    }
}
