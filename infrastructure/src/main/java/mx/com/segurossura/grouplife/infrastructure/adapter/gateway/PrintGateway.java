package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.PrintPort;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.print.PrintRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrintGateway implements PrintPort {

    @Qualifier("printWebClient")
    private final WebClient printWebClient;

    @Override
    public Mono<byte[]> getPDFConsolidated(Policy policy) {

        //DEIMER AQUI VA LA POLIZA ISSUE
        PrintRequest printRequest = new PrintRequest(
                policy.officeId(), policy.productId(), policy.status(),
                policy.policyNumber(), 0, ""
        );

        return this.printWebClient.post()
                .bodyValue(printRequest)
                .retrieve()
                .bodyToMono(byte[].class)
                .doOnSuccess(pdfBytes -> log.info("PDF recibido con tamaño: {} bytes", pdfBytes.length))
                .doOnError(error -> {
                    log.error("Error when consulting pint, body {} error {}", printRequest, error.getMessage(), error);
                    throw new RuntimeException();
                });

    }
}
