package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.PaymentUrlResponseDto;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.openapi.model.PaymentLink200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.PaymentLinkRequestDto;
import mx.com.segurossura.grouplife.openapi.model.PaymentLinkResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class PaymentLinkIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/payment-link/get";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String PAYMENT_LINK = "/issue-portal/get";

    @MockitoBean
    @Qualifier("paymentUrlWebClient")
    private WebClient paymentUrlWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.paymentUrlWebClient.post()).thenReturn(this.requestBodyUriSpec);
        doReturn(this.requestBodySpec)
                .when(this.requestBodyUriSpec)
                .uri(this.uriCaptor.capture());
        when(this.requestBodySpec.bodyValue(any())).thenAnswer(invocation -> this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestHeadersSpec);
    }

    @Test
    void test_paymentLink_withRequestValid_shouldReturnPaymentLinkResponseAndStatus200() {
        final FolioRecordEntity folioRecordEntity = TestFixturesPaymentLink.createFolioEntity();
        folioRecordEntity.getPolicy().getLast().setStatusIssue("COMPLETED");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();
        final PaymentLinkResponseDto data = new PaymentLinkResponseDto();
        data.setUrlRedirect("http://localhost:8081/paymentLink");
        data.setValidity(LocalDate.parse("2025-01-16"));
        data.setSuccess(true);

        when(this.requestBodyUriSpec.uri((Function<UriBuilder, URI>) argThat(argument -> {
            if (!(argument instanceof Function)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            final Function<UriBuilder, URI> uriFunction = (Function<UriBuilder, URI>) argument;
            final UriBuilder uriBuilderMock = UriComponentsBuilder.fromUriString("http://localhost/8009");
            final URI uri = uriFunction.apply(uriBuilderMock);
            return uri.getPath().equals(PAYMENT_LINK);
        }))).thenReturn(this.requestBodySpec);
        final mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.PaymentLinkResponseDto dataMock =
                new mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.PaymentLinkResponseDto(
                        "http://localhost:8081/paymentLink", LocalDate.parse("2025-01-16"), true);
        final PaymentUrlResponseDto paymentUrlResponseDto = new PaymentUrlResponseDto(dataMock);
        when(this.responseSpec.bodyToMono(PaymentUrlResponseDto.class))
                .thenReturn(Mono.just(paymentUrlResponseDto));

        final PaymentLink200ResponseDto expect = new PaymentLink200ResponseDto();
        expect.data(data);
        final PaymentLinkRequestDto request = new PaymentLinkRequestDto();
        request.setNumberFolio("2024");

        final PaymentLink200ResponseDto response =
                this.webTestClient.post()
                        .uri(BASE_PATH)
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .body(BodyInserters.fromValue(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(PaymentLink200ResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertEquals(expect, response);
    }


}
