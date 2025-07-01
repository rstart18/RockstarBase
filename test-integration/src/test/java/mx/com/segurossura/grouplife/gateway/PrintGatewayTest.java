package mx.com.segurossura.grouplife.gateway;

import mx.com.segurossura.grouplife.application.port.PrintPort;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.PrintGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrintGatewayTest {

    private final byte[] bytes = new byte[10];
    @Mock
    private WebClient printWebClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;
    @InjectMocks
    private PrintGateway printGateway;

    @Mock
    private PrintPort printPort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(printWebClient.post()).thenReturn(mock(WebClient.RequestBodyUriSpec.class));
        when(printWebClient.post().bodyValue(any())).thenReturn(mock(WebClient.RequestHeadersSpec.class));
        when(printWebClient.post().bodyValue(any()).retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getPDFConsolidated_Success() {

        final Policy policy = createPolicy();

        // Configura el mock de WebClient para devolver una respuesta exitosa
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(bytes));

        // Verificación
        StepVerifier.create(printGateway.getPDFConsolidated(policy))
                .expectNext(bytes)
                .verifyComplete();
    }

    @Test
    void getPDFConsolidated_Error() {
        // Configura el mock de WebClient para devolver un error
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.error(new RuntimeException()));

        // Verificación
        StepVerifier.create(printGateway.getPDFConsolidated(createPolicy()))
                .expectErrorMatches(throwable -> {
                    // Verifica que el error sea del tipo esperado
                    return throwable instanceof RuntimeException;
                })
                .verify();
    }

    private Policy createPolicy() {
        // Formato de fecha del JSON
        final String dateString = "2025-02-02T22:13:51.289Z";
        final LocalDateTime createdAt = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);

        // Crear instancia de Quote
        final Quote quote = new Quote(
                157L,
                1D,
                902D,
                "W",
                3788D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D
        );

        return Policy.builder()
                .currency("MXN")
                .formPayment(8)
                .periodicity(12)
                .personCode(51975836L)
                .officeId(quote.officeId().intValue())
                .productId(quote.productId().intValue())
                .status(quote.policyState())
                .policyNumber(quote.policyNumber().longValue())
                .folioFromPartner(quote.folio())
                .statusInsureds(true)
                .statusClient(true)
                .createdAt(createdAt)
                .requestId("14")
                .costs(quote)
                .build();
    }

}
