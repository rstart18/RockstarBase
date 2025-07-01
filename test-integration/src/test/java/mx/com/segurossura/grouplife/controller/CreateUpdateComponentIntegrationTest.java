package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.components.ComponentRecordEntity;
import mx.com.segurossura.grouplife.openapi.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CreateUpdateComponentIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/components";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @Autowired
    protected ReactiveMongoTemplate reactiveMongoTemplate;

    @Test
    void test_createCompany_withRequestValid_shouldReturnXomponentPersistence200ResponseDto() {
        //Given
        final String numberFolio = "12";
        final FolioNumber folioNumber = new FolioNumber(numberFolio);

        final ComponentDto componentDto = new ComponentDto();
        final StateDto stateDto = new StateDto();
        componentDto.setState(stateDto);
        componentDto.setComponents(List.of(new ComponentsInnerDto()));
        componentDto.setNumberFolio(numberFolio);
        final CurrentStepDto currentStepDto = new CurrentStepDto();
        currentStepDto.setIndex("2");
        currentStepDto.setSubSteps(List.of("2", "6"));
        componentDto.setCurrentStep(currentStepDto);

        // When
        this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(componentDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateUpdateComponentPersistence200ResponseDto.class);

        // Then
        final ComponentRecordEntity updatedComponentRecord = this.reactiveMongoTemplate.findById(
                folioNumber, ComponentRecordEntity.class).block();
        assertNotNull(updatedComponentRecord, "The folio must not be null");
        assertEquals(componentDto.getCurrentStep().getIndex(), updatedComponentRecord.getCurrentStep().getIndex());
//        assertTrue(updatedFolioRecord.getUpdatedAt().isAfter(updatedFolioRecord.getCreatedAt()), "The update date must be after the creation date");
    }


}
