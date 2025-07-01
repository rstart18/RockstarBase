package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.openapi.model.GetCoveragesByModality200ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GetCoveragesIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/coverages";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @MockitoBean
    @Qualifier("catalogWebClient")
    private WebClient catalogWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setup() {
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);

        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    }

    @Test
    public void test_getCoveragesByModality_success() {

        final InsuranceDataDto mnockCoverage = TestFixtures.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mnockCoverage));

        final GetCoveragesByModality200ResponseDto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH + "?modality=TRADICIONAL")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(GetCoveragesByModality200ResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(actualResponse);
    }
}
