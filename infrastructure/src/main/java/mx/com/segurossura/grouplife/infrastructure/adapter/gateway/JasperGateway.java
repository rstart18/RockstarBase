package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.JasperPort;
import mx.com.segurossura.grouplife.application.port.UtilsPort;
import mx.com.segurossura.grouplife.domain.model.jasper.JasperDataResponse;
import mx.com.segurossura.grouplife.domain.model.jasper.ReportQuotation;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.response.JasperDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import mx.com.segurossura.grouplife.infrastructure.utils.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class JasperGateway implements JasperPort {
    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker folioSequenceCircuitBreaker;
    @Qualifier("folioSequenceRetry")
    private final Retry folioSequenceRetry;
    @Qualifier("muiJasperWebClient")
    private final WebClient muiJasperWebClient;

    private final JasperQuotationMapper jasperQuotationMapper;

    private final UtilsPort utilsPort;

    @Override
    public Mono<JasperDataResponse> getPDFBase64(final ReportQuotation reportQuotation) {
        return this.muiJasperWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/mui-pdfjasper/documents/" + utilsPort.getFileName() + "/get").build())
                .bodyValue(this.jasperQuotationMapper.modelToRequest(reportQuotation))
                .retrieve()
                .bodyToMono(JasperDataResponseDto.class)
                .flatMap(jasperDataResponseDto -> {
                    if (jasperDataResponseDto.data() == null) {
                        log.error("Error when consulting JasperGateway. Returning empty Mono.");
                        return Mono.error(new GatewayException("Error", "VG-GTW-NULL-JASPER"));
                    }
                    final JasperDataResponse jasperDataResponse = this.jasperQuotationMapper.responseToModel(jasperDataResponseDto);
                    return Mono.just(jasperDataResponse);
                })
                .transformDeferred(RetryOperator.of(this.folioSequenceRetry))
                .transformDeferred(CircuitBreakerOperator.of(this.folioSequenceCircuitBreaker))
                .doOnError(error -> log.error("Error when consulting JasperGateway: {} {}", Utils.toJson(this.jasperQuotationMapper.modelToRequest(reportQuotation)), error.getMessage(), error));
    }

}
