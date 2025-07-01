package mx.com.segurossura.grouplife.gateway;

import mx.com.segurossura.grouplife.domain.model.pricing.Pricing;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PlanDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PricingVolunteerResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.PricingGateway;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
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
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class PricingGatewayTest {

    @Mock
    private WebClient storageWebClient;
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
    private PricingGateway pricingGateway;

    @Mock
    private PricingMapper pricingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.storageWebClient.post()).thenReturn(this.requestBodyUriSpec);
        doReturn(this.requestBodySpec)
                .when(this.requestBodyUriSpec)
                .uri(this.uriCaptor.capture());
        when(this.requestBodySpec.bodyValue(any())).thenAnswer(invocation -> this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
    }

    @Test
    void getPricing_data_null() {

        final PlanDataResponseDto storageResponseDto = new PlanDataResponseDto(null);
        final Pricing pricing = null;

        when(responseSpec.bodyToMono(PlanDataResponseDto.class)).thenReturn(Mono.just(storageResponseDto));

        StepVerifier.create(pricingGateway.getPricing(pricing))
                .expectErrorMatches(throwable -> throwable instanceof GatewayException &&
                        ((GatewayException) throwable).getCode().contains("VG-GTW-NULL-PRICING"))
                .verify();
    }

    @Test
    void getPricing_volunteer_data_null() {

        final PricingVolunteerResponseDto storageResponseDto = new PricingVolunteerResponseDto(null);
        final Pricing pricing = null;

        when(responseSpec.bodyToMono(PricingVolunteerResponseDto.class)).thenReturn(Mono.just(storageResponseDto));

        StepVerifier.create(pricingGateway.getPricingVolunteer(pricing))
                .expectErrorMatches(throwable -> throwable instanceof GatewayException &&
                        ((GatewayException) throwable).getCode().contains("VG-GTW-NULL-PRICING"))
                .verify();
    }

}
