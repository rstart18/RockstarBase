package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission.CommissionDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.openapi.model.CatalogItemDto;
import mx.com.segurossura.grouplife.openapi.model.CompanyDto;
import mx.com.segurossura.grouplife.openapi.model.CompanyRequestDto;
import mx.com.segurossura.grouplife.openapi.model.CompanyResponseDto;
import mx.com.segurossura.grouplife.openapi.model.CreateCompany201ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.ModalityDto;
import mx.com.segurossura.grouplife.openapi.model.PlanDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.utils.FolioRecordEntityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class CreateCompanyIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio/{numberFolio}/company";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @MockitoBean
    @Qualifier("catalogWebClient")
    private WebClient catalogWebClient;

    @MockitoBean
    @Qualifier("commissionWebClient")
    private WebClient commissionWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    private static Stream<Arguments> createCompanyWithDataDoesNotComplyWithTheRequirement() {
        final String companyName = "eeeeeeeeeerrrrrrrrrrqqqqqqqqqwwwwwwwwwwwccccccccccccggggggggghhhhhhhhh";
        final CatalogItemDto businessActivity = new CatalogItemDto();
        businessActivity.setKey("key");
        businessActivity.setValue("value");

        final String errorRegex = "Nombre no cumple con el formato requerido.";
        final String errorNotNull = "no debe ser nulo.";
        final String errorSizeCharactereName = "debe tener entre 1 y 60 caracteres.";

        return Stream.of(
                Arguments.of("Request without modality returns invalid modality parameter ",
                        null, "CompanyName", businessActivity, "modality", errorNotNull),
                Arguments.of("Request without company name returns invalid company.name parameter ",
                        ModalityDto.TRADICIONAL, null, businessActivity, "company.name", errorNotNull),
                Arguments.of("Request with company name with the following special character ! must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName!", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character @ must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName@", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character # must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName#", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character $ must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName$", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character % must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName%", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character ^ must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName^", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character * must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName*", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character ( must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName(", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character ) must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName)", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character - must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName-", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character _ must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName_", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character = must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName=", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character + must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName+", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character { must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName{", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character } must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName}", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character [ must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName[", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character ] must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName]", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character : must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName:", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character ; must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName;", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character < must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName<", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character > must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName>", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character , must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName,", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character . must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName.", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character ? must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName?", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character / must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName/", businessActivity, "company.name", errorRegex),
                Arguments.of("Request with company name with the following special character \\ must returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, "companyName\\", businessActivity, "company.name", errorRegex),
                Arguments.of("with company name with more than 60 characters returns invalid company.name parameter",
                        ModalityDto.TRADICIONAL, companyName, businessActivity, "company.name", errorSizeCharactereName),
                Arguments.of("Request without businessActivity",
                        ModalityDto.TRADICIONAL, "CompanyName", null, "company.businessActivity", errorNotNull)
        );
    }

    private static Stream<Arguments> createCompanyWithDifferentModality() {
        final CatalogItemDto catalogItemDto = new CatalogItemDto();
        catalogItemDto.setKey("key");
        catalogItemDto.setValue("value");
        final CompanyDto companyDtoVolunteerRequest = new CompanyDto();
        companyDtoVolunteerRequest.setName("companyVolunteer");
        companyDtoVolunteerRequest.setBusinessActivity(catalogItemDto);
        final CompanyDto companyDtoVolunteerResult = new CompanyDto();
        companyDtoVolunteerResult.setName("companyVolunteer");
        companyDtoVolunteerResult.setBusinessActivity(catalogItemDto);
        companyDtoVolunteerResult.setAverageAgeInsured(41);
        final CompanyDto companyDtoTraditional = new CompanyDto();
        companyDtoTraditional.setName("companyTraditional");
        companyDtoTraditional.setBusinessActivity(catalogItemDto);
        companyDtoTraditional.setAverageAgeInsured(30);
        companyDtoTraditional.setNumAdministrativeInsured(201);
        companyDtoTraditional.setNumOperationalInsured(null);
        companyDtoTraditional.setAdministrativeInsuredSum(BigDecimal.valueOf(49));
        companyDtoTraditional.setOperationalInsuredSum(null);
        final InsuranceDataDto mockInsuranceDataVolunteer = TestFixtures.createInsuranceDataDtoVolunteer();
        final InsuranceDataDto mockInsuranceDataTraditional = TestFixtures.createInsuranceDataDto();
        return Stream.of(
                Arguments.of("create company with traditional modality returns ok and DefaultResponseDto",
                        ModalityDto.TRADICIONAL, companyDtoTraditional, companyDtoTraditional, "TRADICIONAL",
                        mockInsuranceDataTraditional),
                Arguments.of("create company with volunteer modality returns ok and DefaultResponseDto",
                        ModalityDto.VOLUNTARIA, companyDtoVolunteerRequest, companyDtoVolunteerResult, "VOLUNTARIA",
                        mockInsuranceDataVolunteer)
        );
    }

    private static CompanyDto createCompanyDto(final String name, final CatalogItemDto businessActivity,
                                               final Integer averageAgeInsured, final Integer numAdministrativeInsured,
                                               final Integer numOperationalInsured,
                                               final BigDecimal administrativeInsuredSum,
                                               final BigDecimal operationalInsuredSum) {
        final CompanyDto companyDto = new CompanyDto();
        companyDto.setName(name);
        companyDto.setBusinessActivity(businessActivity);
        companyDto.setAverageAgeInsured(averageAgeInsured);
        companyDto.setNumAdministrativeInsured(numAdministrativeInsured);
        companyDto.setNumOperationalInsured(numOperationalInsured);
        companyDto.setAdministrativeInsuredSum(administrativeInsuredSum);
        companyDto.setOperationalInsuredSum(operationalInsuredSum);
        return companyDto;
    }

    private static Stream<Arguments> createCompanyWithErroneousDataInTraditionalModality() {
        final CatalogItemDto businessActivity = new CatalogItemDto();
        businessActivity.setKey("key");
        businessActivity.setValue("value");
        return Stream.of(
                Arguments.of("create company with modality traditional with averageAgeInsured validation",
                        createCompanyDto("companyName", businessActivity, 1, 7, 0, new BigDecimal(150000), new BigDecimal(150000)),
                        "El promedio de la edad de los asegurados debe estar entre 24 y 60."
                ),
                Arguments.of("create company with traditional modality with min numInsured validation",
                        createCompanyDto("companyName", businessActivity, 50, 5, 1, new BigDecimal(150000), new BigDecimal(150000)),
                        "El total de asegurados debe estar entre 7 y 1000."
                ),
                Arguments.of("create company with traditional modality with numOperationalInsured return DefaultResponseDto",
                        createCompanyDto("companyName", businessActivity, 40, 1, 14, new BigDecimal(150000), new BigDecimal(150000)),
                        "El porcentaje de asegurados operativos no puede exceder el 30% de los asegurados adminsitrativos."
                )
        );
    }

    @BeforeEach
    void setUp() {
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);

        // Configuración del catálogo
        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class))
                .thenReturn(Mono.just(TestFixtures.createInsuranceDataDto()));
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);

        // Configuración de la comisión
        when(this.commissionWebClient.post()).thenReturn(this.requestBodyUriSpec);
        doReturn(this.requestBodySpec)
                .when(this.requestBodyUriSpec)
                .uri(this.uriCaptor.capture());
        when(this.requestBodySpec.bodyValue(any())).thenAnswer(invocation -> this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.bodyToMono(CommissionDataResponseDto.class))
                .thenReturn(Mono.just(TestFixtures.createCommissionDataResponseDto()));
    }

    private CatalogItemDto createCatalogItemDto() {
        final CatalogItemDto catalogItemDto = new CatalogItemDto();
        catalogItemDto.setKey("key");
        catalogItemDto.setValue("value");
        return catalogItemDto;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createCompanyWithDifferentModality")
    void test_createCompany_withRequestValid_shouldReturnStatusCreatedAndCreateCompany201ResponseDto(final String testName,
                                                                                                     final ModalityDto modality,
                                                                                                     final CompanyDto companyRequest,
                                                                                                     final CompanyDto companyResult,
                                                                                                     final String modalityResult,
                                                                                                     final InsuranceDataDto mockInsuranceData
    ) {
        //Given
        final String numberFolio = modality.toString().equals("TRADICIONAL") ? "324" : "432";
        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe2")
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final PlanDto planDto = new PlanDto();
        planDto.key("Estandar");
        planDto.setName("estandar");
        final List<String> coverages = new ArrayList<>();
        coverages.add("001");
        planDto.coverages(coverages);
        final CompanyRequestDto companyRequestDto = new CompanyRequestDto();
        companyRequestDto.plan(planDto);
        companyRequestDto.setCompany(companyRequest);
        companyRequestDto.setModality(modality);

        final CompanyResponseDto data = new CompanyResponseDto();
        data.setPlan(planDto);
        data.setCompany(companyResult);
        data.setModality(modalityResult);
        final CreateCompany201ResponseDto expect = new CreateCompany201ResponseDto();
        expect.setData(data);

        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceData));
        // When
        final CreateCompany201ResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH, folioNumber.getNumberFolio())
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(companyRequestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateCompany201ResponseDto.class)
                .returnResult().getResponseBody();

        // Then
        assertEquals(response, expect);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createCompanyWithDataDoesNotComplyWithTheRequirement")
    void test_createCompany_withRequestWithCompanyMissingRequiredFields_shouldInvalidParametersAndStatusBadRequest(
            final String testName, final ModalityDto modality, final String companyName,
            final CatalogItemDto businessActivity, final String field, final String errorDesc) {

        //Given
        final String numberFolio = "333";
        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe2")
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();


        final PlanDto planDto = new PlanDto();
        planDto.key("Estandar");
        planDto.setName("estandar");
        final List<String> coverages = new ArrayList<>();
        coverages.add("001");
        planDto.coverages(coverages);
        final CompanyDto companyDto = createCompanyDto(companyName, businessActivity, 89, 25, 34, new BigDecimal(23), new BigDecimal(23));
        final CompanyRequestDto companyRequestDto = new CompanyRequestDto();
        companyRequestDto.plan(planDto);
        companyRequestDto.setCompany(companyDto);
        companyRequestDto.setModality(modality);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-002");
        errorItem.setDescription(errorDesc);
        errorItem.setField(field);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH, numberFolio)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(companyRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        final List<StandardErrorDto> sortedExpectedErrors = new ArrayList<>(expect.getErrors());
        assert response != null;
        final List<StandardErrorDto> sortedResponseErrors = new ArrayList<>(response.getErrors());

        sortedExpectedErrors.sort(Comparator.comparing(StandardErrorDto::getField));
        sortedResponseErrors.sort(Comparator.comparing(StandardErrorDto::getField));

        // Then
        assertEquals(sortedExpectedErrors, sortedResponseErrors);
    }

    @Test
    void test_createCompany_withRequestWithModalityTraditionalWithoutPlan_shouldReturnCreateCompany201ResponseDtoAndStatusOk() {
        // Given
        final String numberFolio = "43211";
        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe2")
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();
        final CatalogItemDto businessActivity = this.createCatalogItemDto();
        final CompanyDto companyDto = createCompanyDto("name", businessActivity, 49, 150, 34, new BigDecimal(23), new BigDecimal(23));
        final CompanyRequestDto companyRequestDto = new CompanyRequestDto();
        companyRequestDto.plan(null);
        companyRequestDto.setModality(ModalityDto.TRADICIONAL);
        companyRequestDto.setCompany(companyDto);
        final InsuranceDataDto mockInsuranceDataTraditional = TestFixtures.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockInsuranceDataTraditional));
        final CompanyResponseDto data = new CompanyResponseDto();
        data.setCompany(companyDto);
        data.setModality("TRADICIONAL");
        data.setPlan(null);

        final CreateCompany201ResponseDto expect = new CreateCompany201ResponseDto();
        expect.setData(data);

        // When
        final CreateCompany201ResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH, numberFolio)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(companyRequestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateCompany201ResponseDto.class)
                .returnResult().getResponseBody();

        // Then
        assertEquals(expect, response);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createCompanyWithErroneousDataInTraditionalModality")
    void test_createCompany_withRequestInvalid_shouldReturnInvalidParameterException(final String testName,
                                                                                     final CompanyDto companyDto,
                                                                                     final String errorDesc
    ) {
        //Given
        final String folioNumber = "12";
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordEntity("12");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final PlanDto planDto = new PlanDto();
        planDto.key("Estandar");
        planDto.setName("estandar");
        final List<String> coverages = new ArrayList<>();
        coverages.add("001");
        planDto.coverages(coverages);
        final CompanyRequestDto companyRequestDto = new CompanyRequestDto();
        companyRequestDto.plan(planDto);
        companyRequestDto.company(companyDto);
        companyRequestDto.modality(ModalityDto.TRADICIONAL);

        // When
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-003");
        errorItem.setDescription(errorDesc);
        errorItem.setField(null);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        //When
        final WebTestClient.ResponseSpec response = this.webTestClient.post()
                .uri(BASE_PATH, folioNumber)
                .headers(headers -> {
                    headers.setBasicAuth(USERNAME, PASSWORD);
                    headers.add("bearer-token", "eyy");
                })
                .body(BodyInserters.fromValue(companyRequestDto))
                .exchange();

        // Then
        response.expectStatus()
                .isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .isEqualTo(expect);
    }

    @Test
    void test_createFolio_withEmptyRequestBody_shouldReturnInvalidParameters() {
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
    void test_createCompany_withRequestValidWithoutBasicAuthentication_shouldReturnException() {
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
    void test_createCompany_withRequestValidWithFolioRecordNotFound_shouldReturnException() {
        // Given
        final CatalogItemDto businessActivity = this.createCatalogItemDto();
        final PlanDto planDto = new PlanDto();
        planDto.key("Estandar");
        planDto.setName("estandar");
        final List<String> coverages = new ArrayList<>();
        coverages.add("001");
        planDto.coverages(coverages);
        final CompanyDto companyDto = createCompanyDto("name", businessActivity,
                49, 25, 34, new BigDecimal(23), new BigDecimal(23));
        final CompanyRequestDto companyRequestDto = new CompanyRequestDto();
        companyRequestDto.plan(planDto);
        companyRequestDto.setModality(ModalityDto.TRADICIONAL);
        companyRequestDto.setCompany(companyDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-MDB-001");
        errorItem.setDescription("FolioRecordNotFound");
        errorItem.setField(null);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH, 12)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(companyRequestDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        // Then
        assertEquals(expect, response);
    }

    @Test
    void test_createCompany_whenRequestWithDifferentModality_shouldReturnModalityChangeException() {
        // Given
        final CatalogItem catalogItem = new CatalogItem("key", "values");
        final Company company = new Company("name", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final String numberFolio = "9432";
        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe2")
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setModality(String.valueOf(ModalityDto.VOLUNTARIA));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();
        final CatalogItemDto businessActivity = this.createCatalogItemDto();
        final CompanyDto companyDto = createCompanyDto("name", businessActivity,
                49, 25, 34, new BigDecimal(23), new BigDecimal(23));
        final PlanDto planDto = new PlanDto();
        planDto.key("Estandar");
        planDto.setName("estandar");
        final List<String> coverages = new ArrayList<>();
        coverages.add("001");
        planDto.coverages(coverages);
        final CompanyRequestDto companyRequestDto = new CompanyRequestDto();
        companyRequestDto.plan(planDto);
        companyRequestDto.setModality(ModalityDto.TRADICIONAL);
        companyRequestDto.setCompany(companyDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-VS-003");
        errorItem.setDescription("La modalidad de la compañía existente no puede ser alterada.");
        errorItem.setField(null);
        errorItem.setTraceId(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH, 9432)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(companyRequestDto))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        // Then
        assertEquals(expect, response);
    }

}
