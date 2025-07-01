package mx.com.segurossura.grouplife.gateway;

import mx.com.segurossura.grouplife.domain.model.comission.CommissionAgent;
import mx.com.segurossura.grouplife.domain.model.comission.CommissionRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission.CommissionDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.CommissionGateway;
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
import java.time.LocalDate;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class CommissionGatewayTest {

    @Mock
    private WebClient commissionWebClient;
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

    @Mock
    private QuotationDetailsMapper quotationDetailsMapper;

    @InjectMocks
    private CommissionGateway commissionGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.commissionWebClient.post()).thenReturn(this.requestBodyUriSpec);
        doReturn(this.requestBodySpec)
                .when(this.requestBodyUriSpec)
                .uri(this.uriCaptor.capture());
        when(this.requestBodySpec.bodyValue(any())).thenAnswer(invocation -> this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestHeadersSpec);
    }

    @Test
    void getCommission_Success() {

        CommissionRequest commissionRequest = new CommissionRequest(902, "", "", LocalDate.now(), "" , "");
        CommissionAgent commissionAgent = new CommissionAgent(0.2D, 0.01D);

        CommissionDataResponseDto.CommissionResponseDto commissionResponseDto = new CommissionDataResponseDto.CommissionResponseDto(
                902, "", "", LocalDate.now(), "", "",
                0.2D, 0.01D
        );
        CommissionDataResponseDto commissionDataResponseDto = new CommissionDataResponseDto(commissionResponseDto);

        when(this.responseSpec.bodyToMono(CommissionDataResponseDto.class))
                .thenReturn(Mono.just(commissionDataResponseDto));
        when(this.quotationDetailsMapper.responseDtoToDomain(commissionResponseDto)).thenReturn(commissionAgent);

        // Verificación
        StepVerifier.create(commissionGateway.getComissionsAgent(commissionRequest))
                .expectNextMatches(item -> item.commissionPercentageAgent().equals(0.2D))
                .verifyComplete();
    }
}
