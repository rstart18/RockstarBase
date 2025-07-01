package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.Sami;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.openapi.model.DeleteGroup200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardResponseMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static mx.com.segurossura.grouplife.controller.testdata.TestFixturesGroup.createListSami;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeleteGroupIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio/{numberFolio}/groups/{groupId}";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @Test
    void test_deleteGroup_withValidFolioAndNumberGroupId_shouldDeleteGroup200ResponseDtoAndStatusOk() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA", true,
                null, List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null, salaries, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final List<Sami> sami = createListSami();
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, sami,null, null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setSami(BigDecimal.valueOf(36));
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardResponseMessageDto data = new StandardResponseMessageDto();
        data.setMessage("success");
        final DeleteGroup200ResponseDto expect = new DeleteGroup200ResponseDto();
        expect.setData(data);
        // When
        final DeleteGroup200ResponseDto actualResponse = this.webTestClient.delete()
                .uri(BASE_PATH, 8, 2)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(DeleteGroup200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
        final FolioRecordEntity updatedFolioRecord = this.reactiveMongoTemplate
                .findById(folioNumber, FolioRecordEntity.class)
                .block();
        assertNotNull(updatedFolioRecord);
        final boolean groupExists = updatedFolioRecord.getGroups().stream()
                .anyMatch(group -> group.groupNumber() == 2);

        assertFalse(groupExists, "El grupo con ID 2 debería haber sido eliminado de la base de datos.");
    }

    @Test
    void test_deleteGroup_withValidFolioAndNumberGroupIdNotFound_shouldReturnGroupNotFound() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA", true,
                null, List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null, salaries, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final FolioNumber folioNumber = new FolioNumber("8");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("El grupo con ID 23 no existe en este folio.");
        errorItem.setDescription("GroupNotFound");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.delete()
                .uri(BASE_PATH, 8, 23)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_deleteGroup_withInValidFolioAndNumberGroupIdValid_shouldReturnGroupNotFoundAndStatusNotFound() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("8");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-MDB-001");
        errorItem.setDescription("FolioRecordNotFound");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.delete()
                .uri(BASE_PATH, 8222, 23)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_deleteGroup_withValidFolioButLastGroupWithAdministrators_shouldReturnGroupValidationExceptionAndStatusBadRequest() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA", true,
                null, List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));
        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 0, 12, null, new BigDecimal(12), new BigDecimal("12.12"), 12, null
                , salaries, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final FolioNumber folioNumber = new FolioNumber("9");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-GV-047");
        errorItem.setDescription("Debe existir al menos un grupo con administrativos después de eliminar el grupo seleccionado.");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.delete()
                .uri(BASE_PATH, 9, 2)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_deleteGroup_withValidFolioAndSumInsuredWithDependencies_shouldReturnGroupValidationExceptionAndStatusBadRequest() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA", true,
                null, List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));
        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 10, 12, new BigDecimal(1440000), new BigDecimal(12), new BigDecimal("12.12"), 12, null
                , salaries, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "estandar", "SAFIJA", 12, 12, new BigDecimal(1000000), new BigDecimal(1440000),
                new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final FolioNumber folioNumber = new FolioNumber("9");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-GV-048");
        errorItem.setDescription("El grupo con la suma administrativa más alta no puede ser eliminado porque otro grupo tiene una suma operativa que excede el nuevo máximo administrativo.");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.delete()
                .uri(BASE_PATH, 9, 1)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }


    @Test
    void test_deleteGroup_withValidFolioAndModalityVolunteer_shouldDeleteGroup200ResponseDtoAndStatusOk() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA", true,
                null, List.of(insuredValidation), null, null, null, null);

        final GroupVg groupVg = new GroupVg(1, "grupo 016 VOLUNTARIA", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null, null, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "grupo 017 VOLUNTARIA", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null, null, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final List<Sami> sami = createListSami();
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, sami,null, null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        folioRecord.setModalityValidation(modalityValidation);
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardResponseMessageDto data = new StandardResponseMessageDto();
        data.setMessage("success");
        final DeleteGroup200ResponseDto expect = new DeleteGroup200ResponseDto();
        expect.setData(data);
        // When
        final DeleteGroup200ResponseDto actualResponse = this.webTestClient.delete()
                .uri(BASE_PATH, 8, 2)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(DeleteGroup200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
        final FolioRecordEntity updatedFolioRecord = this.reactiveMongoTemplate
                .findById(folioNumber, FolioRecordEntity.class)
                .block();
        assertNotNull(updatedFolioRecord);
        final boolean groupExists = updatedFolioRecord.getGroups().stream()
                .anyMatch(group -> group.groupNumber() == 2);

        assertFalse(groupExists, "El grupo con ID 2 debería haber sido eliminado de la base de datos.");
    }
}
