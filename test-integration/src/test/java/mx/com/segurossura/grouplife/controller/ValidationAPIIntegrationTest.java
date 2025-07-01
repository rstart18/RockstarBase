package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.validation.ValidationAPIResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.openapi.model.ValidateRFC200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.ValidateRFCDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidationAPIIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/validate-rfc";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @MockitoBean
    @Qualifier("validationAPIWebClient")
    private WebClient validationAPIWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;


    @BeforeEach
    void setUp() {

        when(this.validationAPIWebClient.get()).thenReturn(this.mockRequestHeadersUriSpec);
        when(this.mockRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockRequestHeadersSpec);
        when(this.mockRequestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    }

    @Test
    void test_ValidationRFC_PersonaFisica_Success() {

        // Given
        final ValidationAPIResponseDto validationAPIResponseDto = new ValidationAPIResponseDto(true, "RFC Válido", null);
        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.just(validationAPIResponseDto));

        final ValidateRFCDto validateRFCDto = new ValidateRFCDto();
        validateRFCDto.setIsValid(true);
        final ValidateRFC200ResponseDto expect = new ValidateRFC200ResponseDto();
        expect.data(validateRFCDto);

        // When
        final ValidateRFC200ResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=1&rfc=MEHL910804GR3&name=LEONARD&surname=MENDOZA&secondSurname=HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ValidateRFC200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(expect, actualResponse);

        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        verify(mockRequestHeadersUriSpec).uri(uriFunctionCaptor.capture());
        Function<UriBuilder, URI> uriFunction = uriFunctionCaptor.getValue();

        UriBuilder uriBuilder = UriComponentsBuilder.fromPath("");
        URI uri = uriFunction.apply(uriBuilder);

//        System.out.println(uri.toString());

        String expectedUri = "/validate-rfc?tipoPersona=personaFisica&RFC=MEHL910804GR3&nombre=LEONARD&apePaterno=MENDOZA&apeMaterno=HERNANDEZ&fechaNacimiento=1991-08-04";
        assertEquals(expectedUri, uri.toString());
    }

    @Test
    void test_ValidationRFC_PersonaMoral_Success() {
        // Given
        final ValidationAPIResponseDto validationAPIResponseDto = new ValidationAPIResponseDto(true, "RFC Válido", null);
        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.just(validationAPIResponseDto));

        final ValidateRFCDto validateRFCDto = new ValidateRFCDto();
        validateRFCDto.setIsValid(true);
        final ValidateRFC200ResponseDto expect = new ValidateRFC200ResponseDto();
        expect.data(validateRFCDto);

        // When
        final ValidateRFC200ResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=2&rfc=MEH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ValidateRFC200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_ValidationRFC_ShouldMissingRequestValueException() {

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?rfc=MEH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-INPUT-003", actualResponse.getErrors().getFirst().getCode());
    }

    @Test
    void test_ValidationRFC_ShouldConstraintViolationException() {

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=2&rfc=MEHL910804GR3fdgdfg&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-INPUT-004", actualResponse.getErrors().getFirst().getCode());
    }

    @Test
    void test_ValidationRFC_shouldThrowGatewayClientException_RFCNotValid() {

        WebClientResponseException badRequestException = WebClientResponseException.create(
                400,
                "Bad Request",
                HttpHeaders.EMPTY,
                "{\"success\": false, \"renderMessage\":{\"body\":\"El RFC capturado no es válido\"}}".getBytes(),
                null
        );

        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.error(badRequestException));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=2&rfc=RTH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(actualResponse);

    }

    @Test
    void test_ValidationRFC_shouldThrowGatewayClientException() {

        WebClientResponseException badRequestException = WebClientResponseException.create(
                400,
                "Bad Request",
                HttpHeaders.EMPTY,
                "{\"success\": false, \"message\":\"Faltan campos\"}".getBytes(),
                null
        );

        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.error(badRequestException));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=3&rfc=RTH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(actualResponse);

    }

    @Test
    void test_ValidationRFC_shouldThrowGatewayClientException_ObjectMapper() {

        WebClientResponseException badRequestException = WebClientResponseException.create(
                400,
                "Bad Request",
                HttpHeaders.EMPTY,
                "error".getBytes(),
                null
        );

        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.error(badRequestException));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=3&rfc=RTH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(actualResponse);
    }

    @Test
    void test_ValidationRFC_shouldThrowGatewayUnauthorizedException() {

        WebClientResponseException badRequestException = WebClientResponseException.create(
                401,
                "Unauthorized",
                HttpHeaders.EMPTY,
                "error".getBytes(),
                null
        );

        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.error(badRequestException));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=2&rfc=MEH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
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
    }

    @Test
    void test_ValidationRFC_shouldThrowGatewayServerErrorException() {

        WebClientResponseException badRequestException = WebClientResponseException.create(
                500,
                "Server Error",
                HttpHeaders.EMPTY,
                "error".getBytes(),
                null
        );

        when(this.responseSpec.bodyToMono(ValidationAPIResponseDto.class)).thenReturn(Mono.error(badRequestException));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?typeLegalId=2&rfc=MEH910804GR3&name=MENDOZA HERNANDEZ&birthdate=1991-08-04")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals("VG-GTW-SERVER-ERROR", actualResponse.getErrors().getFirst().getCode());
    }

}
