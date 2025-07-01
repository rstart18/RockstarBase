package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalogue.PeopleResponseDto;
import mx.com.segurossura.grouplife.openapi.model.PeopleRfcResponseDto;
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

public class CatalogueIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/people/";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @MockitoBean
    @Qualifier("catalogueWebClient")
    private WebClient catalogueWebClient;

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
        when(this.catalogueWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    }

    @Test
    void test_getReportQuotation_success() {

        final PeopleResponseDto peopleResponseDto = createPeopleResponseDto();
        when(this.responseSpec.bodyToMono(PeopleResponseDto.class)).thenReturn(Mono.just(peopleResponseDto));

        final PeopleRfcResponseDto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH + "/KAT030904SC9")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(PeopleRfcResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(actualResponse);
    }

    private PeopleResponseDto createPeopleResponseDto() {
        return new PeopleResponseDto(
                51893745L,                          // numeroInterno
                "1 - REGISTRO FEDERAL CONTRIBUYENTES", // tipoIdentificador
                "MEHL910804GR3",                    // identificador
                "LEONARD MENDOZA HERNANDEZ",        // nombreCompleto
                "LEONARD",                          // primerNombre
                "JOSE",                             // sewgundoNombre
                "MENDOZA",                          // primerApellido
                "HERNANDEZ",                        // segundoApellido
                "01 - NORMAL",                      // tipoPersona
                "1 - FISICA",                       // personalidadJuridica
                "H - HOMBRE",                       // sexo
                "1991-08-04",                       // fechaNacimiento
                " - ",                              // provincia
                "LEONARDMENDOZAHERNANDEZ",          // nombreClave
                6,                                  // numeroOrdinalDomicio
                "1 - PARTICULAR",                   // tipoDomicilio
                "calle 3",                          // descripcionDomicilio
                " -  ",                             // siglasDomicilio
                " -  ",                             // idioma
                "5562013254",                       // telefono
                "90000",                            // codigoPostal
                "052 - MEXICO",                     // pais
                "127",                              // piso
                "123",                              // numero
                "29 - TLAXCALA",                    // provinciaDomicilio
                "001NP - LA JOYA",                  // colonia
                "5623483746",                       // telefonoContacto
                "test@mail.com",
                "testC@mail.com",
                "5623483746",
                "606",
                "01060",
                "",
                "",
                ""
        );
    }

}
