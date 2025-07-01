package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.openapi.model.AgeLimitDto;
import mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto;
import mx.com.segurossura.grouplife.openapi.model.GetGroups200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryMonthDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.openapi.model.SumDto;
import mx.com.segurossura.grouplife.openapi.model.YearLimitDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetGroupsIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio/{numberFolio}/groups";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @Test
    void test_getGroups_withRequestWithValidFolio_shouldGetGroups200ResponseDtoAndStatusOk() {
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

        final GroupVg groupVg = new GroupVg(12, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null, salaries, List.of(coverageDetail));
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
        folioRecord.setGroups(List.of(groupVg));
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final YearLimitDto maxDto = new YearLimitDto();
        maxDto.setValue(12);
        maxDto.setUnit("YEAR");
        final YearLimitDto minDto = new YearLimitDto();
        minDto.setValue(12);
        minDto.setUnit("YEAR");
        final AgeLimitDto acceptableYearOldLimitDto = new AgeLimitDto();
        acceptableYearOldLimitDto.min(minDto);
        acceptableYearOldLimitDto.max(maxDto);
        final AgeLimitDto renovationYearOldLimitDto = new AgeLimitDto();
        renovationYearOldLimitDto.min(minDto);
        renovationYearOldLimitDto.max(maxDto);
        final AgeLimitDto cancellationYearOldLimitDto = new AgeLimitDto();
        cancellationYearOldLimitDto.min(minDto);
        cancellationYearOldLimitDto.max(maxDto);
        final SumDto sumDto = new SumDto();
        sumDto.formulaDescription(null);
        sumDto.dependencies(null);
        sumDto.setFormula(null);
        sumDto.setDefaultValue(new BigDecimal(500000));
        final InsuredSumLimitDto insuredSumLimitDto = new InsuredSumLimitDto();
        insuredSumLimitDto.max(sumDto);
        insuredSumLimitDto.min(sumDto);
        final InsuredValidationDto insuredValidationDto = new InsuredValidationDto();
        insuredValidationDto.setKinship(null);
        insuredValidationDto.setKinshipKey(null);
        insuredValidationDto.setAcceptableYearOldLimit(acceptableYearOldLimitDto);
        insuredValidationDto.setInsuredSumLimit(List.of(insuredSumLimitDto));
        final CoverageDetailDto coverageDetailDto = new CoverageDetailDto();
        coverageDetailDto.setCoverageKey("FALLECIMIENTO");
        coverageDetailDto.setCode("00001");
        coverageDetailDto.setDescription("Fallecimiento");
        coverageDetailDto.setTypeCoverage("BASICA");
        coverageDetailDto.setMandatory(true);
        coverageDetailDto.setDefaultValue(null);
        coverageDetailDto.setInsuredValidations(List.of(insuredValidationDto));
        coverageDetailDto.setInsuredSumFix(false);

        final GroupVgDto groupVgDto = new GroupVgDto();
        groupVgDto.setGroupNumber(12);
        groupVgDto.setName("estandar");
        groupVgDto.setGroupType("SAFIJA");
        groupVgDto.setNumAdministrativeInsured(12);
        groupVgDto.setNumOperationalInsured(12);
        groupVgDto.setAdministrativeInsuredSum(new BigDecimal(12));
        groupVgDto.setOperationalInsuredSum(new BigDecimal(12));
        groupVgDto.setInsuredSum(new BigDecimal("12.12"));
        groupVgDto.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        groupVgDto.setCoverages(List.of(coverageDetailDto));
        final GetGroups200ResponseDto expect = new GetGroups200ResponseDto();
        expect.setData(List.of(groupVgDto));

        // When
        final GetGroups200ResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GetGroups200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_getGroups_withRequestWithInValidFolio_shouldReturnFolioRecordExceptionAndStatusBadRequest() {
        // Given
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-DB-02");
        errorItem.setDescription("FolioByGroupNotFound");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH, 8)
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
    void test_getGroups_withRequestWithValidFolioAndWithoutGroups_shouldGetGroups200ResponseDtoAndStatusOk() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("18");
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
        final List<GroupVgDto> data = new ArrayList<>();
        final GetGroups200ResponseDto expect = new GetGroups200ResponseDto();
        expect.setData(data);
        // When
        final GetGroups200ResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH, 18)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GetGroups200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }
}
