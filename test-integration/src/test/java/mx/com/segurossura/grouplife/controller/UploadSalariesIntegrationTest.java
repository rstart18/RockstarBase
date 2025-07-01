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
import mx.com.segurossura.grouplife.openapi.model.CreateGroup201ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgResponseDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto;
import mx.com.segurossura.grouplife.openapi.model.SalariesDetailDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryMonthDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryRequestDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryRequestSalariesInnerDto;
import mx.com.segurossura.grouplife.openapi.model.SumDto;
import mx.com.segurossura.grouplife.openapi.model.YearLimitDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadSalariesIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio/{numberFolio}/groups/{groupNumber}/upload";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";


    @Test
    void test_getGroups_withRequestWithValidFolio_shouldGetGroups200ResponseDtoAndStatusOk() {
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

        final GroupVg groupVg = new GroupVg(12, "estandar", "SAFIJA", 2, 1, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null, salaries, List.of(coverageDetail));
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
        coverageDetailDto.setInsuredValidations(List.of(insuredValidationDto));
        coverageDetailDto.setInsuredSumFix(false);
        final SalariesDetailDto sal1 = new SalariesDetailDto()
                .insured("Asegurado 1")
                .salary(15000.0)
                .activity(SalariesDetailDto.ActivityEnum.valueOf("ADMINISTRATIVOS"));

        final SalariesDetailDto sal2 = new SalariesDetailDto()
                .insured("Asegurado 2")
                .salary(18000.0)
                .activity(SalariesDetailDto.ActivityEnum.valueOf("OPERATIVOS"));

        final SalariesDetailDto sal3 = new SalariesDetailDto()
                .insured("Asegurado 3")
                .salary(20000.0)
                .activity(SalariesDetailDto.ActivityEnum.valueOf("ADMINISTRATIVOS"));

        final List<SalariesDetailDto> sals = List.of(sal1, sal2, sal3);

        final GroupVgResponseDto groupVgDto = new GroupVgResponseDto();
        groupVgDto.setGroupNumber(12);
        groupVgDto.setName("estandar");
        groupVgDto.setGroupType("SAFIJA");
        groupVgDto.setNumAdministrativeInsured(2);
        groupVgDto.setNumOperationalInsured(1);
        groupVgDto.setAdministrativeInsuredSum(new BigDecimal(12));
        groupVgDto.setOperationalInsuredSum(new BigDecimal(12));
        groupVgDto.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        groupVgDto.setSalaries(sals);
        groupVgDto.setCoverages(List.of(coverageDetailDto));
        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(groupVgDto);

        final SalaryRequestSalariesInnerDto salary1 = new SalaryRequestSalariesInnerDto()
                .insured("Asegurado 1")
                .salary(15000.0)
                .activity("ADMINISTRATIVOS");

        final SalaryRequestSalariesInnerDto salary2 = new SalaryRequestSalariesInnerDto()
                .insured("Asegurado 2")
                .salary(18000.0)
                .activity("OPERATIVOS");

        final SalaryRequestSalariesInnerDto salary3 = new SalaryRequestSalariesInnerDto()
                .insured("Asegurado 3")
                .salary(20000.0)
                .activity("ADMINISTRATIVOS");

        final SalaryRequestDto requestDto = new SalaryRequestDto();
        requestDto.setSalaries(List.of(salary1, salary2, salary3));

        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8, 12)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertEquals(expect, actualResponse);
    }
}
