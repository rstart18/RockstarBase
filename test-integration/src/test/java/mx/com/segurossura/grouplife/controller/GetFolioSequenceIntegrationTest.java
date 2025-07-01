package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.folio.FolioResponseDto;
import mx.com.segurossura.grouplife.openapi.model.FolioDto;
import mx.com.segurossura.grouplife.openapi.model.GetFolioSequence200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetFolioSequenceIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @MockitoBean
    @Qualifier("folioSequenceWebClient")
    private WebClient folioSequenceWebClient;

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
        when(this.folioSequenceWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    }

    @Test
    void test_getFolioSequence_shouldReturnFolioSequence() {
        //Given
        final FolioResponseDto.FolioData folioData = new FolioResponseDto.FolioData();
        folioData.setNumberFolio(122L);
        folioData.setPartnerId("partnerId");
        folioData.setProduct("product");
        final FolioResponseDto folioResponseDto = new FolioResponseDto();
        folioResponseDto.setData(folioData);
        when(this.responseSpec.bodyToMono(FolioResponseDto.class)).thenReturn(Mono.just(folioResponseDto));

        final FolioDto folioDto = new FolioDto();
        folioDto.setNumberFolio(122);
        final GetFolioSequence200ResponseDto expect = new GetFolioSequence200ResponseDto();
        expect.setData(folioDto);
        // When
        final GetFolioSequence200ResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GetFolioSequence200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(expect, actualResponse);
        final Function<UriBuilder, URI> capturedUriFunction = this.uriCaptor.getValue();
        final UriBuilder uriBuilderMock = mock(UriBuilder.class);

        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(any(), Optional.ofNullable(any()))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(URI.create("/folios?partnerId=COTIZADORES_SURA&solution=RC"));

        capturedUriFunction.apply(uriBuilderMock);

        verify(uriBuilderMock).path("/folios");
        verify(uriBuilderMock).queryParam("partnerId", "COTIZADORES_SURA");
        verify(uriBuilderMock).queryParam("solution", "VIDAGRUPO");
    }

    @Test
    void test_getFolioSequence_withoutBasicAuthentication_shouldReturnDefaultErrorResponseDto() {
        //Given
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-SEC-001");
        errorItem.setDescription("unauthorized");
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
}
