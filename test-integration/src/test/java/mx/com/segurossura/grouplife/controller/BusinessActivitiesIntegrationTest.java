package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog.CatalogListDto;
import mx.com.segurossura.grouplife.infrastructure.configuration.webclient.CatalogWebClientConfiguration;
import mx.com.segurossura.grouplife.infrastructure.configuration.webclient.WebClientErrorHandlingUtils;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import mx.com.segurossura.grouplife.openapi.model.CatalogItemDto;
import mx.com.segurossura.grouplife.openapi.model.GetBusinessActivities200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BusinessActivitiesIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/business-activities";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @Mock
    ClientResponse clientResponse;

    @Mock
    ExchangeFunction exchangeFilterFunction;

    @MockitoBean
    @Qualifier("catalogWebClient")
    private WebClient catalogWebClient;

    @InjectMocks
    private CatalogWebClientConfiguration catalogWebClientConfiguration;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setUp() {
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
        when(this.exchangeFilterFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(this.clientResponse));
    }

    @Test
    void test_businessActivities_withRequestValid_shouldReturnStatusCreatedAndGetBusinessActivities200ResponseDto() {
        // Given
        final CatalogItemDto catalogItemDto = new CatalogItemDto();
        catalogItemDto.setValue("key");
        catalogItemDto.setValue("value");
        final CatalogListDto catalogListDto = new CatalogListDto(Collections.singletonList(catalogItemDto));
        when(this.responseSpec.bodyToMono(CatalogListDto.class)).thenReturn(Mono.just(catalogListDto));

        final GetBusinessActivities200ResponseDto expect = new GetBusinessActivities200ResponseDto();
        expect.data(List.of(catalogItemDto));

        // When
        final GetBusinessActivities200ResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GetBusinessActivities200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(expect, actualResponse);
        final Function<UriBuilder, URI> capturedUriFunction = this.uriCaptor.getValue();
        final UriBuilder uriBuilderMock = mock(UriBuilder.class);

        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(anyString(), anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(URI.create("/catalogs/902?catalog=GIROS"));

        capturedUriFunction.apply(uriBuilderMock);
        verify(uriBuilderMock).path("/catalogs/902");
        verify(uriBuilderMock).queryParam("catalog", "GIROS");
    }

    @Test
    void test_getBusinessActivities_shouldThrowGatewayUnauthorizedException() {
        // Given
        when(this.clientResponse.statusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(this.clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Unauthorized access"));
        when(this.exchangeFilterFunction.exchange(any())).thenReturn(Mono.just(this.clientResponse));
        when(this.responseSpec.bodyToMono(CatalogListDto.class))
                .thenReturn(Mono.error(new GatewayException.GatewayUnauthorizedException("Unauthorized access")));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-GTW-UNAUTHORIZED", actualResponse.getErrors().getFirst().getCode());
        assertEquals("Unauthorized access", actualResponse.getErrors().getFirst().getDescription());
    }

    @Test
    void test_getBusinessActivities_shouldThrowGatewayServerErrorException() {
        // Given
        when(this.responseSpec.bodyToMono(CatalogListDto.class))
                .thenReturn(Mono.error(new GatewayException.GatewayServerErrorException("Server error occurred")));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-GTW-SERVER-ERROR", actualResponse.getErrors().getFirst().getCode());
        assertEquals("Server error occurred", actualResponse.getErrors().getFirst().getDescription());
    }

    @Test
    void test_getBusinessActivities_shouldReturnError_whenServerError() {
        // Given
        final CatalogListDto catalogListDto = new CatalogListDto(null);
        when(this.responseSpec.bodyToMono(CatalogListDto.class)).thenReturn(Mono.just(catalogListDto));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-GTW-NO-ACTIVITIES", actualResponse.getErrors().getFirst().getCode());
        assertEquals("No business activities found.", actualResponse.getErrors().getFirst().getDescription());
    }

    @Test
    void test_getBusinessActivities_shouldReturnClientError_whenClientError() {
        when(this.responseSpec.bodyToMono(CatalogListDto.class))
                .thenReturn(Mono.error(new GatewayException.GatewayClientErrorException("Client error occurred")));

        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(actualResponse);
        assertEquals("VG-GTW-CLIENT-ERROR", actualResponse.getErrors().getFirst().getCode());
        assertEquals("Client error occurred", actualResponse.getErrors().getFirst().getDescription());
    }

    @Test
    void test_getBusinessActivities_withoutBasicAuthentication_shouldReturnException() {
        //Given
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setDescription("unauthorized");
        errorItem.setCode("VG-SEC-001");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        //When
        final WebTestClient.ResponseSpec response = this.webTestClient.get()
                .uri(BASE_PATH)
                .exchange();

        // Then
        response.expectStatus()
                .isUnauthorized()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }

    @Test
    void test_getBusinessActivities_catalogWebClient_shouldThrowGatewayClientErrorException_on4xxError() {
        // Given
        final String errorBody = "Internal Server Error";
        final HttpStatus serverErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        when(this.clientResponse.statusCode()).thenReturn(serverErrorStatus);
        when(this.clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
        when(this.exchangeFilterFunction.exchange(any())).thenReturn(Mono.just(this.clientResponse));

        // When
        when(this.responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new GatewayException.GatewayClientErrorException("Client error occurred")));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-SERVER", actualResponse.getErrors().getFirst().getCode());
    }

    @Test
    void test_getBusinessActivities_UnauthorizedErrorHandling() {
        // Given
        when(this.clientResponse.statusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(this.clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Unauthorized access"));
        when(this.exchangeFilterFunction.exchange(any())).thenReturn(Mono.just(this.clientResponse)); // Mockeando el comportamiento del exchange

        // When
        ExchangeFilterFunction filter = WebClientErrorHandlingUtils.errorHandlingFilter();
        final Mono<ClientResponse> responseMono = filter.filter(ClientRequest.create(HttpMethod.GET, URI.create("/test")).build(), this.exchangeFilterFunction);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof GatewayException.GatewayUnauthorizedException &&
                        throwable.getMessage().contains("Unauthorized access"))
                .verify();
    }

    @Test
    void testClientErrorHandling() {
        // Given
        final String errorBody = "Bad Request";
        final HttpStatus clientErrorStatus = HttpStatus.BAD_REQUEST;

        when(this.clientResponse.statusCode()).thenReturn(clientErrorStatus);
        when(this.clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
        when(this.exchangeFilterFunction.exchange(any())).thenReturn(Mono.just(this.clientResponse));

        // When
        ExchangeFilterFunction filter = WebClientErrorHandlingUtils.errorHandlingFilter();
        final Mono<ClientResponse> responseMono = filter
                .filter(ClientRequest.create(HttpMethod.GET, URI.create("/test")).build(), this.exchangeFilterFunction);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof GatewayException.GatewayClientErrorException &&
                        throwable.getMessage().contains("Client error: Status 400 BAD_REQUEST, details: Bad Request"))
                .verify();
    }

    @Test
    void testServerErrorHandling() {
        // Given
        final String errorBody = "Internal Server Error";
        final HttpStatus serverErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        when(this.clientResponse.statusCode()).thenReturn(serverErrorStatus);
        when(this.clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
        when(this.exchangeFilterFunction.exchange(any())).thenReturn(Mono.just(this.clientResponse));

        // When
        ExchangeFilterFunction filter = WebClientErrorHandlingUtils.errorHandlingFilter();
        final Mono<ClientResponse> responseMono = filter
                .filter(ClientRequest.create(HttpMethod.GET, URI.create("/test")).build(), this.exchangeFilterFunction);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof GatewayException.GatewayServerErrorException &&
                        throwable.getMessage().contains("Server error: Status 500 INTERNAL_SERVER_ERROR, details: Internal Server Error"))
                .verify();
    }

    @Test
    void testSuccessfulResponseHandling() {
        // Given
        final HttpStatus successStatus = HttpStatus.OK;
        when(this.clientResponse.statusCode()).thenReturn(successStatus);

        // When
        ExchangeFilterFunction filter = WebClientErrorHandlingUtils.errorHandlingFilter();
        final Mono<ClientResponse> responseMono = filter
                .filter(ClientRequest.create(HttpMethod.GET, URI.create("/test")).build(), this.exchangeFilterFunction);

        // Then
        StepVerifier.create(responseMono)
                .expectNext(this.clientResponse)
                .verifyComplete();
    }
}

