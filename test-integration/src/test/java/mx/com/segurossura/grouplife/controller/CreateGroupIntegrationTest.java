package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestCoverages;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.controller.testdata.TestFixturesCoverages;
import mx.com.segurossura.grouplife.controller.testdata.TestFixturesCoveragesByModalityVolunteer;
import mx.com.segurossura.grouplife.controller.testdata.TestFixturesGroup;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.Sami;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.infrastructure.configuration.webclient.CatalogWebClientConfiguration;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.openapi.model.CreateGroup201ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgRequestDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgResponseDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesCoveragesInformationInnerDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryMonthDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static mx.com.segurossura.grouplife.controller.testdata.TestFixturesGroup.createListSami;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateGroupIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio/{numberFolio}/groups";
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

    private static Stream<Arguments> createGroupWithDataValid() {
        final List<InsuredSumCoveragesDto> insuredSumCoverages =
                TestFixturesCoverages.createInsuredSumCoveragesInfoDto();
        final List<InsuredSumCoveragesDto> insuredSumCoverages1 =
                TestFixturesCoverages.createInsuredSumCoveragesInfoDtoRequest1();
        final List<InsuredSumCoveragesDto> insuredSumCoverages2 =
                TestFixturesCoverages.createInsuredSumCoveragesInfoDtoRequest2();
        final GroupVgResponseDto data = TestFixturesGroup.createGroupVgResponseDtoSA_FIJA();
        final GroupVgResponseDto data1 = TestFixturesGroup.createGroupVgResponseDtoSA_FIJAData1();
        return Stream.of(
                Arguments.of("create group with 0 operative and 1 administrative", 1, 0, null, new BigDecimal(1440000), data1, insuredSumCoverages1),
                Arguments.of("create group with administratives and  operatives", 2, 1, new BigDecimal(1000000), new BigDecimal(1440000), data,
                        insuredSumCoverages)
        );
    }

    private static Stream<Arguments> createGroupWithDataValidAndMonthsSalary() {
        final InsuredSumCoveragesDto coverage1 = new InsuredSumCoveragesDto();
        final InsuredSumCoveragesCoveragesInformationInnerDto insured = new InsuredSumCoveragesCoveragesInformationInnerDto();
        insured.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        insured.setInsuredSum(BigDecimal.valueOf(1000000));
        coverage1.setCode("9842");
        coverage1.setCoveragesInformation(List.of(insured));
        final List<InsuredSumCoveragesDto> coverages2 = new ArrayList<>();
        coverages2.add(coverage1);
        return Stream.of(
                Arguments.of("create group with 0 administrative and 1 operative and Months Salary", 1, 2, new BigDecimal(1200000),
                        new BigDecimal(14400000),
                        new BigDecimal(14400000), 1, coverages2, "data2")
        );
    }

    private static Stream<Arguments> createGroupWithErroneousDataOnTheInsuredAmounts() {
        final InsuredSumCoveragesDto coverage1 = new InsuredSumCoveragesDto();
        final InsuredSumCoveragesCoveragesInformationInnerDto insured = new InsuredSumCoveragesCoveragesInformationInnerDto();
        insured.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        insured.setInsuredSum(BigDecimal.valueOf(1000000));
        coverage1.setCode("9842");
        coverage1.setCoveragesInformation(List.of(insured));
        final List<InsuredSumCoveragesDto> coverages2 = new ArrayList<>();
        coverages2.add(coverage1);
        final InsuredSumCoveragesDto coverages = TestFixturesGroup.createInsuredSumCoveragesDto();
        final GroupVgRequestDto request1 = TestFixturesGroup.createGroupVgRequestDtoSA_FIJADataInvalid("name 23",
                GroupVgRequestDto.GroupTypeEnum.SA_FIJA, null, null, new BigDecimal(1400000), new BigDecimal(1400000),
                List.of(coverages));
        final GroupVgRequestDto request2 = TestFixturesGroup.createGroupVgRequestDtoSA_FIJADataInvalid("name 23",
                GroupVgRequestDto.GroupTypeEnum.SA_FIJA, 1, 0, null, new BigDecimal(1400000),
                List.of(coverages));
        final GroupVgRequestDto request3 = TestFixturesGroup.createGroupVgRequestDtoSA_FIJADataInvalid("name 23",
                GroupVgRequestDto.GroupTypeEnum.SA_FIJA, 1, 0, new BigDecimal(1440000), null,
                coverages2);
        return Stream.of(
                Arguments.of("create group sin un integrante", request1, "VG-CV-029"),
                Arguments.of("create group sin un integranteen operativos y con suma asegurada para operativos",
                        request2, "VG-CV-035"),
                Arguments.of("create group con almenos un administrativo en la lista de coberturas debe venir una " +
                                "suma para el type de administrativos",
                        request3, "VG-CV-033")
        );
    }

    private static Stream<Arguments> createGroupWithDataInvalidToValidationsMSD() {
        final InsuredSumCoveragesDto coverage2 = new InsuredSumCoveragesDto();
        final InsuredSumCoveragesDto coverage1 = new InsuredSumCoveragesDto();
        final InsuredSumCoveragesCoveragesInformationInnerDto insured = new InsuredSumCoveragesCoveragesInformationInnerDto();
        insured.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        insured.setInsuredSum(BigDecimal.valueOf(1000000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insured2 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insured2.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        insured2.setInsuredSum(BigDecimal.valueOf(1440000));
        coverage1.setCode("9842");
        coverage1.setCoveragesInformation(List.of(insured));
        coverage2.setCode("9842");
        coverage2.setCoveragesInformation(List.of(insured2));
        return Stream.of(
                Arguments.of("create group con almenos un administrativo en suma asegurada no puede ser nula",
                        100000.0, new BigDecimal(1000000), new BigDecimal(1000000), 0, 1, List.of(coverage1), "VG-CV-030"),
                Arguments.of("si biene almenos 1 administrativo la suma asegurada para administrativos no puede ser " +
                        "null", 120000.0, null, null, 1, 0, List.of(coverage1), "VG-CV-035"),
                Arguments.of("create group con 0 operativos no debe venir una suma asegurada para operativos en la " +
                                "lista de coverages",
                        120000.0, null, new BigDecimal(1440000), 1, 0, List.of(coverage1), "VG-CV-033"),
                Arguments.of("create group con almenos un operativos en la lista de coberturas debe venir una " +
                                "suma para el type de operativos",
                        120000.0, null, new BigDecimal(1440000), 1, 1, List.of(coverage2), "VG-CV-036"),
                Arguments.of("si no hay asegurados administrativos no debe haber una suma asegurada para " +
                                "administrativos",
                        100000.0, new BigDecimal(1000000), new BigDecimal(1000000), 1, 0, List.of(coverage2), "VG-CV-031"),
                Arguments.of("debe venir con almenos un integrante el grupo en meses sueldo",
                        100000.0, new BigDecimal(1000000), new BigDecimal(1000000), 0, 0, List.of(coverage2), "VG-CV-029")
        );
    }


    private static Stream<Arguments> createGroupWithDataInvalidToValidations() {
        final List<InsuredSumCoveragesDto> insuredSumCoverages = TestFixturesCoverages.createInsuredSumCoveragesInfoDtoMS();
        final List<InsuredSumCoveragesDto> insuredSumErrorCoverages =
                TestFixturesCoverages.createInsuredSumCoveragesInfoDtoErrorMS();
        final List<InsuredSumCoveragesDto> insuredSumErrorCoverages2 =
                TestFixturesCoverages.createInsuredSumCoveragesInfoDtoError2MS();
        return Stream.of(
                Arguments.of("create con mal averageMonthlySalary", SalaryMonthDto.NUMBER_36,
                        52000.0, new BigDecimal(1440000), new BigDecimal(1440000), 10, 5, "VG-CV-013", insuredSumCoverages,
                        "InvalidGroupConfigurationException"),
                Arguments.of("create con mal administrativeInsuredSum", SalaryMonthDto.NUMBER_12,
                        12000.0, new BigDecimal(1440000), new BigDecimal(2440000), 10, 5, "VG-CV-013", insuredSumCoverages,
                        "InvalidGroupConfigurationException"),
                Arguments.of("create con mal code no existe", SalaryMonthDto.NUMBER_12,
                        12000.0, new BigDecimal(1440000), new BigDecimal(1440000), 10, 5, "VG-CV-018", insuredSumErrorCoverages, "CoverageNotFound"),
                Arguments.of("create con mal suma asegurada optional cobertura",
                        SalaryMonthDto.NUMBER_12,
                        120000.0, new BigDecimal(1440000), new BigDecimal(1440000), 10, 5, "VG-CV-017", insuredSumErrorCoverages2,
                        "InvalidGroupConfigurationException")

        );
    }

    private static Stream<Arguments> createGroupWithDataInvalid() {
        final InsuredSumCoveragesDto insures = TestFixturesGroup.createInsuredSumCoveragesDto();

        final InsuredSumCoveragesDto insuredValidOperati = TestFixturesGroup.createInsuredSumCoveragesDto();
        final List<InsuredSumCoveragesDto> insuredSumCoveragesDtos =
                TestFixturesGroup.createInsuredSumCoveragesInvalidDto();
        insuredSumCoveragesDtos.add(insuredValidOperati);
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        return Stream.of(
                Arguments.of("create group with a code of a coverage that does not exist must return " +
                                "CoverageNotFound", insuredSumCoveragesDtos, null, GroupVgRequestDto.GroupTypeEnum.SA_FIJA,
                        null, company, null, 8, "VG-CV-018", "CoverageNotFound", null),
                Arguments.of("create group numero mayor a 10 company", insures, null,
                        GroupVgRequestDto.GroupTypeEnum.SA_FIJA,
                        null, company, 15, 8, "VG-INPUT-002", "debe ser menor " +
                                "o igual a 10.", "groupNumber"),
                Arguments.of("create group without company", insures, null, GroupVgRequestDto.GroupTypeEnum.SA_FIJA,
                        null, null,
                        null, 8, "VG-VS-008", "CompanyNotFound", null),
                Arguments.of("Create group with groupType SA_FIJA and salary month configured, should fail", insures,
                        SalaryMonthDto.NUMBER_12, GroupVgRequestDto.GroupTypeEnum.SA_FIJA, null,
                        company, null, 8, "VG-VS-009", "InvalidGroupConfigurationException", null),
                Arguments.of("Create group with groupType SA_FIJA and average monthly salary configured, should fail"
                        , insures, null, GroupVgRequestDto.GroupTypeEnum.SA_FIJA, 1200.0, company, null, 8, "VG-VS-010",
                        "InvalidGroupConfigurationException", null),
                Arguments.of("Crear grupo con typo meses sueldo y sin la cantidad de meses retorna error and code " +
                                "VG-VS-011", insures, null, GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO, 1200.0,
                        company, null, 8, "VG-VS-011", "InvalidGroupConfigurationException", null),
                Arguments.of("Creating a group with type months salary and without average salary returns an error " +
                                "and code VG-VS-011", insures, SalaryMonthDto.NUMBER_12,
                        GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO, null, company, null, 8, "VG-VS-011",
                        "InvalidGroupConfigurationException", null)
        );
    }

    @BeforeEach
    void setUp() {
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class))
                .thenReturn(Mono.just(TestFixtures.createInsuranceDataDto()));
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    }

    //@Test
    void test_createGroup_withRequestValidWithGroupDoesNotExist_shouldReturnGroupNotFoundAndStatusNotFound() {
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
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg2 = new GroupVg(2, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg3 = new GroupVg(1, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        groupVgs.add(groupVg2);
        groupVgs.add(groupVg3);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GroupVgRequestDto request = TestFixturesGroup.createGroupVgRequestDtoAndGroupNumberDoesNotExist();

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-010");
        errorItem.setDescription("GroupNotFound");
        errorItem.setField(null);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(StandardErrorDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    //@Test
    void test_createGroup_withRequestValidWithValidFolio_shouldReturnCreateGroup201ResponseDtoAndStatusCreated() {
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
        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 1, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final FolioNumber folioNumber = new FolioNumber("16");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Insured insured = new Insured(7, 1000, null);
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GroupVgRequestDto request = TestFixturesGroup.createGroupVgRequestDtoAndGroupNumberDoesNotExist();

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-010");
        errorItem.setDescription("GroupNotFound");
        errorItem.setField(null);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 901)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(StandardErrorDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createGroupWithDataInvalidToValidations")
    void test_createGroup_withRequestInvalidWithDataDynamically_shouldReturnException(final String testName,
                                                                                      final SalaryMonthDto salaryMonthEnum,
                                                                                      final Double averageMonthlySalary,
                                                                                      final BigDecimal operationalInsuredSum,
                                                                                      final BigDecimal administrativeInsuredSum,
                                                                                      final Integer numAdministrativeInsured,
                                                                                      final Integer operationalInsured,
                                                                                      final String code,
                                                                                      final List<InsuredSumCoveragesDto> insuredSumCoverages,
                                                                                      final String description) {
        // Given
        final FolioNumber folioNumber = new FolioNumber("99");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final List<Sami> samiList = Arrays.asList(
                new Sami(7, 24, 2, 2500000L),
                new Sami(25, 49, 3, 2550000L),
                new Sami(50, 99, 4, 3100000L),
                new Sami(100, 149, 5, 3600000L),
                new Sami(150, 199, 6, 4200000L),
                new Sami(200, 299, 7, 4500000L),
                new Sami(300, 399, 8, 4800000L),
                new Sami(400, 499, 9, 5000000L),
                new Sami(500, 649, 10, 5000000L),
                new Sami(650, 799, 11, 5000000L),
                new Sami(800, 999, 12, 5000000L),
                new Sami(1000, 1000, 13, 5000000L));
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("grupo 3 test");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(numAdministrativeInsured);
        request.setNumOperationalInsured(operationalInsured);
        request.setAdministrativeInsuredSum(administrativeInsuredSum);
        request.setOperationalInsuredSum(operationalInsuredSum);
        request.setSalaryMonth(salaryMonthEnum);
        request.setAverageMonthlySalary(averageMonthlySalary);
        request.setInsuredSumCoverages(insuredSumCoverages);
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode(code);
        errorItem.setDescription(description);

        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 99)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestWithInvalidDataInTheSalaryMonthsFlow_shouldReturnException() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("99");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = TestFixturesCoverages.createGroupVgRequestDtoErrorM_S();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-013");
        errorItem.setDescription("InvalidGroupConfigurationException");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 99)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidWithValidFolioAndGroupTypeMS_shouldReturnCreateGroup201ResponseDtoAndStatusCreated() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("99");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final List<Sami> sami = createListSami();
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, sami,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = TestFixturesCoverages.createGroupVgRequestDtoM_S();

        final GroupVgResponseDto data = TestFixturesCoverages.createGroupVgResponseDtoMs();

        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(data);

        // When
        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 99)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createGroupWithDataInvalidToValidationsMSD")
    void test_createGroup_withRequestValidMSDinamyc_shouldReturnStandardErrorResponseDto(final String testName,
                                                                                         final Double averageMonthlySalary,
                                                                                         final BigDecimal operationalInsuredSum,
                                                                                         final BigDecimal administrativeInsuredSum,
                                                                                         final Integer numAdministrativeInsured,
                                                                                         final Integer operationalInsured,
                                                                                         final List<InsuredSumCoveragesDto> insuredSumCoverages,
                                                                                         final String code) {
        // Given
        final FolioNumber folioNumber = new FolioNumber("17");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto requestDto = new GroupVgRequestDto();
        requestDto.setGroupNumber(null);
        requestDto.setName("nameGroup");
        requestDto.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        requestDto.setNumAdministrativeInsured(numAdministrativeInsured);
        requestDto.setNumOperationalInsured(operationalInsured);
        requestDto.setAdministrativeInsuredSum(administrativeInsuredSum);
        requestDto.setOperationalInsuredSum(operationalInsuredSum);
        requestDto.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        requestDto.setAverageMonthlySalary(averageMonthlySalary);
        requestDto.setInsuredSumCoverages(insuredSumCoverages);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode(code);
        errorItem.setDescription("InvalidGroupConfigurationException");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 17)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidPeroExcedioElLimiteDeGrupos_shouldReturnStandardErrorResponseDtoAndBadRequest() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("17");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final GroupVg groupVg1 = new GroupVg(1, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg2 = new GroupVg(2, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg3 = new GroupVg(3, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg4 = new GroupVg(4, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg5 = new GroupVg(5, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg6 = new GroupVg(6, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg7 = new GroupVg(7, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg8 = new GroupVg(8, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg9 = new GroupVg(9, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg10 = new GroupVg(10, "estandar", "SAFIJA", 1, 1, new BigDecimal(1), new BigDecimal(1), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        groupVgs.add(groupVg2);
        groupVgs.add(groupVg3);
        groupVgs.add(groupVg4);
        groupVgs.add(groupVg5);
        groupVgs.add(groupVg6);
        groupVgs.add(groupVg7);
        groupVgs.add(groupVg8);
        groupVgs.add(groupVg9);
        groupVgs.add(groupVg10);
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final InsuredSumCoveragesDto inureds1 = new InsuredSumCoveragesDto();
        inureds1.setCode("9842");
        inureds1.setCoveragesInformation(TestFixturesCoverages.createInsuresInformationDtoMS());
        final InsuredSumCoveragesDto inureds2 = new InsuredSumCoveragesDto();
        inureds2.setCode("9843");
        inureds2.setCoveragesInformation(TestFixturesCoverages.createInsuresInformationDtoMS());
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("name");
        final List<InsuredSumCoveragesDto> coveragesDtos = new ArrayList<>();
        coveragesDtos.add(inureds1);
        coveragesDtos.add(inureds2);
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(1);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        request.setAverageMonthlySalary(120000.0);
        request.setInsuredSumCoverages(coveragesDtos);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-009");
        errorItem.setDescription("GroupLimitExceeded");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 17)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidAndModalityVolunteer_shouldReturnCreateGroup201ResponseDtoAndStatusCreated() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("78999");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 70, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataVolunteer =
                TestFixturesCoveragesByModalityVolunteer.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataVolunteer));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setName("nameGroupVolunteer");
        request.setInsuredSumCoverages(TestFixturesCoveragesByModalityVolunteer.createInsuredSumCoveragesDtoVolunteer());

        final GroupVgResponseDto data = TestFixturesCoveragesByModalityVolunteer.createGroupVgResponseDto();

        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(data);

        // When
        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 78999)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidButIsNotRoomForMoreGroups_shouldStandardErrorResponseDtoAndStatusBadRequest() {
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
        final GroupVg groupVg1 = new GroupVg(1, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg2 = new GroupVg(2, "estandar", "SAFIJA", 2, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg3 = new GroupVg(3, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg4 = new GroupVg(4, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg5 = new GroupVg(5, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg6 = new GroupVg(6, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg7 = new GroupVg(7, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg8 = new GroupVg(8, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg9 = new GroupVg(9, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg10 = new GroupVg(10, "estandar", "SAFIJA", 1, 1, new BigDecimal(12), new BigDecimal(1), new BigDecimal("12.12"), null, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        groupVgs.add(groupVg2);
        groupVgs.add(groupVg3);
        groupVgs.add(groupVg4);
        groupVgs.add(groupVg5);
        groupVgs.add(groupVg6);
        groupVgs.add(groupVg7);
        groupVgs.add(groupVg8);
        groupVgs.add(groupVg9);
        groupVgs.add(groupVg10);
        final FolioNumber folioNumber = new FolioNumber("994");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 30, 80, 20, new BigDecimal(100000), new BigDecimal(5000000));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = TestFixturesCoverages.createGroupVgRequestDtoM_S();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-009");
        errorItem.setDescription("GroupLimitExceeded");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 994)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidWithValidButToUpdateAGroup_shouldReturnCreateGroup201ResponseDtoAndStatusCreated() {
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
                null, List.of(insuredValidation), null, null, null, false);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 2, 2, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final FolioNumber folioNumber = new FolioNumber("99");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final List<Sami> sami = createListSami();
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, sami, null,null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = TestFixturesCoverages.createGroupVgRequestDtoM_SToUpdate();

        final GroupVgResponseDto data = TestFixturesCoverages.createGroupVgResponseDtoMsToUpdate();

        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(data);

        // When
        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 99)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createGroupWithDataValid")
    void test_createGroup_withRequestValidAndGroupOfTypeSumInsured_shouldReturnCreateGroup201ResponseDtoAndStatusCreated(final String testName,
                                                                                                                         final Integer numAdministrativeInsured,
                                                                                                                         final Integer numOperationalInsured,
                                                                                                                         final BigDecimal operationalSumInsured,
                                                                                                                         final BigDecimal administrativeSumInsured,
                                                                                                                         final GroupVgResponseDto data,
                                                                                                                         final List<InsuredSumCoveragesDto> insuredSumCoverages) {
        // Given
        final FolioNumber folioNumber = new FolioNumber("6");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final List<Sami> sami = createListSami();
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, sami, null,null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(numAdministrativeInsured);
        request.setNumOperationalInsured(numOperationalInsured);
        request.setAdministrativeInsuredSum(administrativeSumInsured);
        request.setOperationalInsuredSum(operationalSumInsured);
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(insuredSumCoverages);

        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(data);

        // When
        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 6)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    //    @ParameterizedTest(name = "{0}")
//    @MethodSource("createGroupWithDataInvalid")
    void test_createGroup_withRequestValidWithDataInvalid_shouldReturnExceptionAndStatusBadRequest(final String testName,
                                                                                                   final List<InsuredSumCoveragesDto> insures,
                                                                                                   final SalaryMonthDto salaryMonthDto,
                                                                                                   final GroupVgRequestDto.GroupTypeEnum groupTypeEnum,
                                                                                                   final Double averageMonthlySalary,
                                                                                                   final Company company,
                                                                                                   final Integer groupNumber,
                                                                                                   final int folioId,
                                                                                                   final String code,
                                                                                                   final String description,
                                                                                                   final String field) {
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
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(groupNumber);
        request.setName("nameGroup");
        request.setGroupType(groupTypeEnum);
        request.setNumAdministrativeInsured(12);
        request.setNumOperationalInsured(12);
        request.setAdministrativeInsuredSum(new BigDecimal(13));
        request.setOperationalInsuredSum(new BigDecimal(13));
        request.setSalaryMonth(salaryMonthDto);
        request.setAverageMonthlySalary(averageMonthlySalary);
        request.setInsuredSumCoverages(insures);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode(code);
        errorItem.setDescription(description);
        errorItem.setField(field);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, folioId)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidWithoutBasicAuthentication_shouldReturnExceptionUnauthorized() {
        //Given
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-SEC-001");
        errorItem.setDescription("unauthorized");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        //When
        final WebTestClient.ResponseSpec response = this.webTestClient.post()
                .uri(BASE_PATH, "12455")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Then
        response.expectStatus()
                .isUnauthorized()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }

    @Test
    void test_createGroup_withEmptyRequestBody_shouldReturnInvalidRequestBody() {
        //Given
        final String numberFolio = "21";
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-001");
        errorItem.setDescription("Invalid request body");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        //When
        final WebTestClient.ResponseSpec response = this.webTestClient.post()
                .uri(BASE_PATH, numberFolio)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Then
        response.expectStatus()
                .isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }

    @Test
    void test_createGroup_withRequestValidWithFolioRecordNotFound_shouldReturnFolioRecordNotFound() {
        // Given
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(1);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1000000));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDto());
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-MDB-001");
        errorItem.setDescription("FolioRecordNotFound");
        errorItem.setField(null);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 25)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createGroupWithErroneousDataOnTheInsuredAmounts")
    void test_createGroup_withRequestWithDataDynamicInvalid_shouldReturnInvalidParameterException(
            final String testName, final GroupVgRequestDto groupVgRequestDto, final String code) {
        // Given
        final FolioNumber folioNumber = new FolioNumber("28");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode(code);
        errorItem.setDescription("InvalidGroupConfigurationException");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 28)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(groupVgRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidButDoesDotDaveACompanyAssociatedWithWheFolio_shouldReturnCompanyException() {
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        folioRecord.setModalityValidation(modalityValidation);
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(1);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1000000));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDto());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-008");
        errorItem.setDescription("CompanyNotFound");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    //@Test
    void test_createGroup_withRequestWithValidFolioAndExceedLimit_shouldExceptionAndStatusBadRequest() {
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

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg2 = new GroupVg(2, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GroupVg groupVg3 = new GroupVg(1, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        groupVgs.add(groupVg2);
        groupVgs.add(groupVg3);
        final FolioNumber folioNumber = new FolioNumber("99");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-021");
        errorItem.setDescription("InvalidGroupConfigurationException");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(12);
        request.setNumOperationalInsured(12);
        request.setAdministrativeInsuredSum(new BigDecimal(13));
        request.setOperationalInsuredSum(new BigDecimal(13));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        final InsuredSumCoveragesDto insures = TestFixturesGroup.createInsuredSumCoveragesDto();
        final List<InsuredSumCoveragesDto> insuresSum = new ArrayList<>();
        insuresSum.add(insures);
        request.setInsuredSumCoverages(insuresSum);
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 99)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    //@Test
    void test_createGroup_withRequestWithoutAnyUserAndSA_FIJA_shouldReturnExceptionAndBadRequest() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("6");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null,null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(null);
        request.setNumOperationalInsured(null);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1000000));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDto());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-029");
        errorItem.setDescription("InvalidGroupConfigurationException");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 9349)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestWithoutAnyUserAndMonthsSalary_shouldReturnExceptionAndBadRequest() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("2333");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null,null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(null);
        request.setNumOperationalInsured(null);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        request.setAverageMonthlySalary(17280.000);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDto());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-029");
        errorItem.setDescription("InvalidGroupConfigurationException");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 2333)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }


    //    @ParameterizedTest(name = "{0}")
//    @MethodSource("createGroupWithDataValidAndMonthsSalary")
    void test_createGroup_withRequestValidWithDataDynamically_shouldReturnCreateGroup201ResponseDtoAndStatusCreated(final String testName,
                                                                                                                    final Integer numAdministrativeInsured,
                                                                                                                    final Integer numOperationalInsured,
                                                                                                                    final GroupVgResponseDto data) {
        // Given
        final FolioNumber folioNumber = new FolioNumber("68");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null,null, null, null, null, null);
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2, 0.01,
                LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("grupo Meses Sueldo");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(numAdministrativeInsured);
        request.setNumOperationalInsured(numOperationalInsured);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        request.setAverageMonthlySalary(120000.0);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDtoMS());

        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(data);

        // When
        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 68)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestInValidAdministrativeSumOutOfRange_shouldReturnInvalidGroupConfigurationException() {
        // Given
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("grupo Meses Sueldo");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(1);
        request.setNumOperationalInsured(0);
        request.setAdministrativeInsuredSum(new BigDecimal(5400000));
        request.setOperationalInsuredSum(null);
        request.setSalaryMonth(SalaryMonthDto.NUMBER_36);
        request.setAverageMonthlySalary(150000.0);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDto23());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-042-MAX");
        errorItem.setDescription("InvalidGroupConfigurationException");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestInValidOperativeSumOutOfRange_shouldReturnGroupValidationExceptionAndStatusBadRequest() {
        // Given
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("grupo Meses Sueldo");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(0);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(null);
        request.setOperationalInsuredSum(new BigDecimal(5400000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_36);
        request.setAverageMonthlySalary(150000.0);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDto24());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-GV-045");
        errorItem.setDescription("El Folio debe tener un grupo con asegurados administrativos.");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestValidButUpdateOnlyGroupWithAdmins_shouldReturnInvalidGroupValidationExceptionAndStatusBadRequest() {
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

        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 1, 12, new BigDecimal(1200000), new BigDecimal(12), new BigDecimal("12.12"), 12,
                null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
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
        final Age age = new Age(12, 65, 24, 60, null);
        final Insured insured = new Insured(7, 1000, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final InsuranceDataDto mockInsuranceDataTraditional = TestCoverages.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(1);
        request.setName("grupo Meses Sueldo");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(0);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(null);
        request.setOperationalInsuredSum(new BigDecimal(1000000));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(TestFixturesCoverages.createInsuredSumCoveragesInfoDtoSF());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-GV-049");
        errorItem.setDescription("Debe existir al menos un grupo con asegurados administrativos.");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestWithValidAndModalityVolunteerByUpdate_shouldReturnCreateGroup201ResponseDtoAndStatusCreated() {
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
        final GroupVg groupVg = new GroupVg(1, "nameGroupVolunteer", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null, null, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "grupo 017 VOLUNTARIA", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null,null, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final FolioNumber folioNumber = new FolioNumber("78999");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 70, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataVolunteer =
                TestFixturesCoveragesByModalityVolunteer.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataVolunteer));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(1);
        request.setName("nameGroupVolunteer");
        request.setInsuredSumCoverages(TestFixturesCoveragesByModalityVolunteer.createInsuredSumCoveragesDtoVolunteer());

        final GroupVgResponseDto data = TestFixturesCoveragesByModalityVolunteer.createGroupVgResponseDto();

        final CreateGroup201ResponseDto expect = new CreateGroup201ResponseDto();
        expect.setData(data);

        // When
        final CreateGroup201ResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 78999)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateGroup201ResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }


    @Test
    void test_createGroup_withRequestInValid_shouldReturnInvalidGroupConfigurationByVolunteerExceptionExceptionAndStatusBadRequest() {
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
        final GroupVg groupVg = new GroupVg(1, "nameGroupVolunteer", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null, null, List.of(coverageDetail));
        final GroupVg groupVg1 = new GroupVg(2, "grupo 017 VOLUNTARIA", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null, null, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        groupVgs.add(groupVg1);
        final FolioNumber folioNumber = new FolioNumber("789990");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 70, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setGroups(groupVgs);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataVolunteer =
                TestFixturesCoveragesByModalityVolunteer.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataVolunteer));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(1);
        request.setName("nameGroupVolunteer");
        request.setInsuredSumCoverages(TestFixturesCoveragesByModalityVolunteer.createInsuredSumCoveragesDtoVolunteerError());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-013");
        errorItem.setDescription("A la cobertura con código '9843' le faltan categorías obligatorias con sumas aseguradas: [H, D, A]");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 789990)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestWithInValidInsuredsSum_shouldReturnInvalidGroupConfigurationByVolunteerExceptionExceptionAndStatusBadRequest() {
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
        final GroupVg groupVg = new GroupVg(1, "nameGroupVolunteer", null, 0, 0, null, null, new BigDecimal("12.12")
                , null, null, null, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg);
        final FolioNumber folioNumber = new FolioNumber("7899901");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 70, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataVolunteer =
                TestFixturesCoveragesByModalityVolunteer.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataVolunteer));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(1);
        request.setName("nameGroupVolunteer");
        request.setInsuredSumCoverages(TestFixturesCoveragesByModalityVolunteer.createInsuredSumCoveragesDtoVolunteerErrorInSums());

        final String expectedCode = "VG-GVMV-04";

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 7899901)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getErrors());
        assertFalse(actualResponse.getErrors().isEmpty());
        assertEquals(expectedCode, actualResponse.getErrors().getFirst().getCode());
    }

    @Test
    void test_createGroup_withRequestWithGroupNotFound_shouldReturnInvalidGroupConfigurationByVolunteerExceptionExceptionAndStatusBadRequest() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("7899901");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 70, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataVolunteer =
                TestFixturesCoveragesByModalityVolunteer.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataVolunteer));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(9);
        request.setName("nameGroupVolunteer");
        request.setInsuredSumCoverages(TestFixturesCoveragesByModalityVolunteer.createInsuredSumCoveragesDtoVolunteer());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-010");
        errorItem.setDescription("GroupNotFound");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 7899901)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();
        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createGroup_withRequestWithInValidInsuredsSumInNUll_shouldReturnInvalidGroupConfigurationByVolunteerExceptionExceptionAndStatusBadRequest() {
        // Given
        final FolioNumber folioNumber = new FolioNumber("7899901");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John DoeEEEEE")
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
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Company company = new Company("company", catalogItem, 12, 70, 12, new BigDecimal(12), new BigDecimal(12));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setCompany(company);
        folioRecord.setModality("VOLUNTARIA");
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();

        final InsuranceDataDto mockInsuranceDataVolunteer =
                TestFixturesCoveragesByModalityVolunteer.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataVolunteer));

        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroupVolunteer");
        request.setInsuredSumCoverages(TestFixturesCoveragesByModalityVolunteer.createInsuredSumCoveragesDtoVolunteerErrorInSumsIn98430());

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-019");
        errorItem.setDescription("La categoría 'C' en la cobertura '9843' tiene una suma asegurada nula.");
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 7899901)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

}
