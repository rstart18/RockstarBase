package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.openapi.model.GetStatus200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StatusDto;
import mx.com.segurossura.grouplife.openapi.model.SystemDto;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class StatusControllerTest {
    private static final String BASE_PATH = "/status";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @InjectMocks
    private StatusController statusController; // Nombre del controlador real

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.webTestClient = WebTestClient.bindToController(statusController).build();
    }


    @Test
    void test_status_withRequestValid_shouldReturnStatusOKAndGetStatus200ResponseDto() {

        when(mongoTemplate.executeCommand("{ ping: 1 }"))
                .thenReturn(Mono.just(new Document("ok", 1)));

        List<SystemDto> systems = new ArrayList<>();
        SystemDto system = new SystemDto();
        system.setStatus(true);
        system.setSystem("MongoDB");
        SystemDto system2 = new SystemDto();
        system2.setStatus(true);
        system2.setSystem("Endpoint");
        systems.add(system);
        systems.add(system2);

        StatusDto status = new StatusDto();
        status.setSystems(systems);

        GetStatus200ResponseDto expectedResponse = new GetStatus200ResponseDto();
        expectedResponse.setData(status);

        this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetStatus200ResponseDto.class)
                .consumeWith(response -> {
                    GetStatus200ResponseDto actualResponse = response.getResponseBody();
                    assert actualResponse != null;
                    assertEquals(expectedResponse.getData().getSystems().getFirst().getSystem(), actualResponse.getData().getSystems().getFirst().getSystem());
                    assertEquals(expectedResponse.getData().getSystems().getFirst().getStatus(), actualResponse.getData().getSystems().getFirst().getStatus());
                });
    }

    @Test
    void test_status_withRequestValid_shouldReturnError() {

        when(mongoTemplate.executeCommand("{ ping: 1 }"))
                .thenReturn(Mono.error(new RuntimeException()));

        this.webTestClient.get()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}
