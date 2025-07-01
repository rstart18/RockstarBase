package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.FolioSequenceGateway;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.openapi.model.CreateFolio201ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.FolioRecordDto;
import mx.com.segurossura.grouplife.openapi.model.FolioRequestDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CreateFolioIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @MockitoBean
    FolioSequenceGateway folioSequenceGateway;

    private static Stream<Arguments> createFolioRecordWithDynamicData() {

        final String agentGroupId = "00001";
        final String agentSubGroupId = "0000100001";

        final String errorNotNull = "no debe ser nulo.";
        final String errorEmail = "debe ser una dirección de correo válida.";

        return Stream.of(
                Arguments.of("Request without userId", null, "pos001", agentGroupId, agentSubGroupId, "rateProfile123",
                        "John Doe", "johndoe@email.com", "office001", "Main Office",
                        "agent001", "Jane Doe", "Promoter Inc.", "promoter001", "userId", errorNotNull),
                Arguments.of("Request without pointOfSaleId", "userId", null, agentGroupId, agentSubGroupId,
                        "rateProfile123", "John Doe", "johndoe@email.com", "office001", "Main Office", "agent001",
                        "Jane Doe", "Promoter Inc.", "promoter001", "pointOfSaleId", errorNotNull),
                Arguments.of("Request without groupId", "userId", "pos001", null, agentSubGroupId,
                        "rateProfile123", "John Doe", "johndoe@email.com", "office001", "Main Office", "agent001",
                        "Jane Doe", "Promoter Inc.", "promoter001", "groupId", errorNotNull),
                Arguments.of("Request without subgroupId", "userId", "pos001", agentGroupId, null,
                        "rateProfile123", "John Doe", "johndoe@email.com", "office001", "Main Office", "agent001",
                        "Jane Doe", "Promoter Inc.", "promoter001", "subgroupId", errorNotNull),
                Arguments.of("Request without rateProfileId", "userId", "pos001", agentGroupId, agentSubGroupId,
                        null, "John Doe", "johndoe@email.com", "office001", "Main Office", "agent001", "Jane Doe",
                        "Promoter Inc.", "promoter001", "rateProfileId", errorNotNull),
                Arguments.of("Request without name", "userId", "pos001", agentGroupId, agentSubGroupId,
                        "rateProfile123", null, "johndoe@email.com", "office001", "Main Office", "agent001", "Jane Doe",
                        "Promoter Inc.", "promoter001", "name", errorNotNull),
                Arguments.of("Request without email", "userId", "pos001", agentGroupId, agentSubGroupId,
                        "rateProfile123", "John Doe", null, "office001", "Main Office", "agent001", "Jane Doe",
                        "Promoter Inc.", "promoter001", "email", errorNotNull),
                Arguments.of("Request without officeId", "userId", "pos001", agentGroupId, agentSubGroupId,
                        "rateProfile123", "John Doe", "johndoe@email.com", null, "Main Office", "agent001", "Jane Doe",
                        "Promoter Inc.", "promoter001", "officeId", errorNotNull),
                Arguments.of("Request without agentId", "userId", "pos001", agentGroupId, agentSubGroupId,
                        "rateProfile123", "John Doe", "johndoe@email.com", "office001", "Main Office", null, "Jane Doe",
                        "Promoter Inc.", "promoter001", "agentId", errorNotNull),
                Arguments.of("Request without agentName", "userId", "pos001", agentGroupId, agentSubGroupId,
                        "rateProfile123", "John Doe", "johndoe@email.com", "office001", "Main Office", "agent001", null,
                        "Promoter Inc.", "promoter001", "agentName", errorNotNull),
                Arguments.of("Request by email with the appropriate format", "userId", "pos001", agentGroupId, agentSubGroupId,
                        "rateProfile123", "John Doe", "johndoeemail.com", "office001", "Main Office", "agent001", "Jane Doe",
                        "Promoter Inc.", "promoter001", "email", errorEmail)
        );
    }

    private static Stream<Arguments> createFolioRecordWithDynamicDataReturnCreated() {
        return Stream.of(
                Arguments.of("Request without officeDescription returns status created and CreateFolio201ResponseDto", null,
                        "Promoter Inc.", "promoter001"),
                Arguments.of("Request with officeDescription returns status created and CreateFolio201ResponseDto",
                        "Main " +
                                "Office", "Promoter Inc.", "promoter001"),
                Arguments.of("Request without promoterName returns status created and CreateFolio201ResponseDto", "Main " +
                        "Office", null, "promoter001"),
                Arguments.of("Request with promoterName returns status created and CreateFolio201ResponseDto", "Main Office"
                        , "Promoter Inc.", "promoter001"),
                Arguments.of("Request without promoterId returns status created and CreateFolio201ResponseDto", "Main " +
                        "Office", "Promoter Inc.", null, "promoter001"),
                Arguments.of("Request with promoterId returns status created and CreateFolio201ResponseDto", "Main Office",
                        "Promoter Inc.", "promoter001", "promoter001")
        );
    }

    Mono<FolioRecordDto> findFolioRecordDtoByNumberFolio(final FolioNumber id) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return this.reactiveMongoTemplate.findOne(query, FolioRecordEntity.class)
                .map(folio -> new FolioRecordDto(id.getNumberFolio()));

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createFolioRecordWithDynamicDataReturnCreated")
    void test_createFolio_withRequestValid_shouldReturnStatusCreatedAndCreateFolio201ResponseDto(
            final String testName, final String officeDescription, final String promoterName, final String promoterId) {
        // Given
        final Folio folio = Folio.builder()
                .numberFolio(123L)
                .build();
        final FolioRequestDto folioRequestDto = new FolioRequestDto();
        folioRequestDto.setUserId("user123");
        folioRequestDto.setPointOfSaleId("pos001");
        folioRequestDto.setGroupId("00001");
        folioRequestDto.setSubgroupId("0000100001");
        folioRequestDto.setRateProfileId("rateProfile123");
        folioRequestDto.name("John Doe");
        folioRequestDto.setEmail("johndoe@email.com");
        folioRequestDto.setOfficeId("office001");
        folioRequestDto.setOfficeDescription(officeDescription);
        folioRequestDto.setAgentId("agent001");
        folioRequestDto.setAgentName("agent001");
        folioRequestDto.setPromoterName(promoterName);
        folioRequestDto.setPromoterId(promoterId);

        // When
        when(this.folioSequenceGateway.getFolioSequence()).thenReturn(Mono.just(folio));

        // Then
        final CreateFolio201ResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(folioRequestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateFolio201ResponseDto.class)
                .returnResult().getResponseBody();

        final FolioNumber folioNumber = new FolioNumber();
        folioNumber.setNumberFolio("123");
        final Mono<FolioRecordDto> result = this.findFolioRecordDtoByNumberFolio(folioNumber);
        final FolioRecordDto folioResponseDto = result.block();
        final FolioRecordDto data = new FolioRecordDto();
        assert folioResponseDto != null;
        data.setNumberFolio(folioResponseDto.getNumberFolio());
        final CreateFolio201ResponseDto expect = new CreateFolio201ResponseDto();
        expect.setData(data);
        assertEquals(expect, response);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createFolioRecordWithDynamicData")
    void test_createFolio_withRequestMissingRequiredFields_shouldInvalidParametersAndStatusBadRequest(
            final String testName, final String userId, final String pointOfSaleId, final String groupId,
            final String subgroupId, final String rateProfileId, final String name, final String email,
            final String officeId, final String officeDescription, final String agentId, final String agentName,
            final String promoterName, final String promoterId, final String field, final String errorField) {
        // Given
        final FolioRequestDto folioRequestDto = new FolioRequestDto();
        folioRequestDto.setUserId(userId);
        folioRequestDto.setPointOfSaleId(pointOfSaleId);
        folioRequestDto.setGroupId(groupId);
        folioRequestDto.setSubgroupId(subgroupId);
        folioRequestDto.setRateProfileId(rateProfileId);
        folioRequestDto.name(name);
        folioRequestDto.setEmail(email);
        folioRequestDto.setOfficeId(officeId);
        folioRequestDto.setOfficeDescription(officeDescription);
        folioRequestDto.setAgentId(agentId);
        folioRequestDto.setAgentName(agentName);
        folioRequestDto.setPromoterName(promoterName);
        folioRequestDto.setPromoterId(promoterId);
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-002");
        errorItem.setDescription(errorField);
        errorItem.setField(field);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(folioRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        //Then
        assertEquals(expect, response);
    }


    @Test
    void test_createFolio_withRequestValidWithoutBasicAuthentication_shouldReturnNotAuthorized() {
        //Given
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-SEC-001");
        errorItem.setDescription("unauthorized");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        //When
        final WebTestClient.ResponseSpec response = this.webTestClient.post()
                .uri(BASE_PATH)
                .exchange();

        // Then
        response.expectStatus()
                .isUnauthorized()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }

    @Test
    void test_createFolio_withRequestValidWithFolioNotExists_shouldReturnFolioNotFound() {
        //Given
        final FolioRequestDto folioRequestDto = new FolioRequestDto();
        folioRequestDto.setUserId("user123");
        folioRequestDto.setPointOfSaleId("pos001");
        folioRequestDto.setGroupId("00001");
        folioRequestDto.setSubgroupId("0000100001");
        folioRequestDto.setRateProfileId("rateProfile123");
        folioRequestDto.setName("John Doe");
        folioRequestDto.setEmail("johndoe@email.com");
        folioRequestDto.setOfficeId("office001");
        folioRequestDto.setOfficeDescription("officeDescription");
        folioRequestDto.setAgentId("agent001");
        folioRequestDto.setAgentName("Jane Doe");
        folioRequestDto.setPromoterName("promoterName");
        folioRequestDto.setPromoterId("promoterId");

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-FSG-001");
        errorItem.setDescription("Folio not found");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        final FolioException.FolioNotFound folioNotFound = new FolioException.FolioNotFound("Folio not found");

        //When
        when(this.folioSequenceGateway.getFolioSequence()).thenThrow(folioNotFound);
        final WebTestClient.ResponseSpec response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .body(BodyInserters.fromValue(folioRequestDto))
                .exchange()
                .expectStatus().isBadRequest();
        // Then
        response.expectStatus()
                .isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }

    @Test
    void test_createFolio_withEmptyRequestBody_shouldReturnInvalidParameters() {
        //Given
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-001");
        errorItem.setDescription("Invalid request body");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        //When
        final WebTestClient.ResponseSpec response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Then
        response.expectStatus()
                .isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }
}
