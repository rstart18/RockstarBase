package mx.com.segurossura.grouplife.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.component.JwtUtilDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CatalogDataDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.ComparatorDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageCatalogDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageDetailDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DiffAdminOpDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredSumLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredValidationDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.LimitBasic;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.SamiDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.SumDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.YearLimitDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.EncryptionException;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.components.ComponentRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.utils.EncryptionUtil;
import mx.com.segurossura.grouplife.infrastructure.utils.JwtUtil;
import mx.com.segurossura.grouplife.openapi.model.CreateUpdateComponentPersistence200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.GetCoveragesByModality200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.RecoverComponentDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.utils.ComponentRecordEntityUtil;
import mx.com.segurossura.grouplife.utils.FolioRecordEntityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GetComponentRecoverIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/components";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String BASE_PATH_CATALOG = "/coverages";

    @MockitoBean
    @Qualifier("catalogWebClient")
    private WebClient catalogWebClient;

    @MockitoBean
    private EncryptionUtil encryptionUtilMock;
    @MockitoBean
    private JwtUtil jwtUtilMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;
    @Mock
    private CatalogPort catalogPort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    }

    @Test
    void test_getComponentRecover_success() {

        final InsuranceDataDto mockCoverage = createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockCoverage));

        //Component
        final String numberFolio = "45";
        final ComponentRecordEntity componentRecordEntity = ComponentRecordEntityUtil.createBaseComponentRecordEntity(numberFolio);
        this.reactiveMongoTemplate.insert(componentRecordEntity).block();
        final RecordFolio agentData = RecordFolio.builder()
                .userId("OPS$")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("Agente.Sura@segurossura.com.mx")
                .officeId("1")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordWithAgentEntity(numberFolio, agentData);
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final String token = "ey";

        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        final String dataEncrypted = "shjajsja";
        final String dataDecrypted = "{\"cotizacionId\":\"45\",\"email\":\"Agente.Sura@segurossura.com.mx\",\"oficinaId\":1,\"ramoId\":\"902\"}";

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.just(JwtUtilDto.builder()
                .value(dataEncrypted)
                .origen("externo").build()
        ));
        Mockito.when(this.encryptionUtilMock.decrypt(anyString())).thenReturn(Mono.just(dataDecrypted));

        final GetCoveragesByModality200ResponseDto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH_CATALOG + "?modality=TRADICIONAL")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(GetCoveragesByModality200ResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(actualResponse);

        // When
        final CreateUpdateComponentPersistence200ResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateUpdateComponentPersistence200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Objects.requireNonNull(response).getData().getNumberFolio(), numberFolio);
    }

    @Test
    void test_getComponentRecover_successPasarela() {

        final InsuranceDataDto mockCoverage = createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockCoverage));

        //Component
        final String numberFolio = "46";
        final ComponentRecordEntity componentRecordEntity = ComponentRecordEntityUtil.createBaseComponentRecordEntity(numberFolio);
        this.reactiveMongoTemplate.insert(componentRecordEntity).block();
        final RecordFolio agentData = RecordFolio.builder()
                .userId("OPS$")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("Agente.Sura@segurossura.com.mx")
                .officeId("1")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordWithAgentEntity(numberFolio, agentData);
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final String token = "ey";

        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        final String dataEncrypted = "shjajsja";
        final String dataDecrypted = "{\"cotizacionId\":\"46\",\"email\":\"Agente.Sura@segurossura.com.mx\",\"oficinaId\":1,\"ramoId\":\"902\"}";

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.just(JwtUtilDto.builder()
                .value(dataEncrypted)
                .origen("pasarela").build()
        ));
        Mockito.when(this.encryptionUtilMock.decrypt(anyString())).thenReturn(Mono.just(dataDecrypted));

        final GetCoveragesByModality200ResponseDto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH_CATALOG + "?modality=TRADICIONAL")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(GetCoveragesByModality200ResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(actualResponse);

        // When
        final CreateUpdateComponentPersistence200ResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateUpdateComponentPersistence200ResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Objects.requireNonNull(response).getData().getNumberFolio(), numberFolio);
    }

    @Test
    void test_getComponentRecover_expired() {

        final InsuranceDataDto mockCoverage = createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockCoverage));

        //Component
        final String numberFolio = "48";
        final ComponentRecordEntity componentRecordEntity = ComponentRecordEntityUtil.createBaseComponentRecordEntity(numberFolio);
        this.reactiveMongoTemplate.insert(componentRecordEntity).block();
        final RecordFolio agentData = RecordFolio.builder()
                .userId("OPS$")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("Agente.Sura@segurossura.com.mx")
                .officeId("1")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordExpired(numberFolio, agentData);
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final String token = "ey";

        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        final String dataEncrypted = "shjajsja";
        final String dataDecrypted = "{\"cotizacionId\":\"48\",\"email\":\"Agente.Sura@segurossura.com.mx\",\"oficinaId\":1,\"ramoId\":\"902\"}";

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.just(JwtUtilDto.builder()
                .value(dataEncrypted)
                .origen("externo").build()
        ));
        Mockito.when(this.encryptionUtilMock.decrypt(anyString())).thenReturn(Mono.just(dataDecrypted));

        // When
        this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void test_getComponentRecover_companyValidator() {

        final InsuranceDataDto mockCoverage = createInsuranceAgeDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mockCoverage));

        //Component
        final String numberFolio = "50";
        final ComponentRecordEntity componentRecordEntity = ComponentRecordEntityUtil.createBaseComponentRecordEntity(numberFolio);
        this.reactiveMongoTemplate.insert(componentRecordEntity).block();
        final RecordFolio agentData = RecordFolio.builder()
                .userId("OPS$")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("Agente.Sura@segurossura.com.mx")
                .officeId("1")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordWithAgentEntity(numberFolio, agentData);
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final String token = "ey";

        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        final String dataEncrypted = "shjajsja";
        final String dataDecrypted = "{\"cotizacionId\":\"50\",\"email\":\"Agente.Sura@segurossura.com.mx\",\"oficinaId\":1,\"ramoId\":\"902\"}";

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.just(JwtUtilDto.builder()
                .value(dataEncrypted)
                .origen("externo").build()
        ));
        Mockito.when(this.encryptionUtilMock.decrypt(anyString())).thenReturn(Mono.just(dataDecrypted));

        // When
        this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void test_getComponentRecover_EncryptionExceptionJwt() {

        final String token = "ey";
        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.error(new EncryptionException.JwtException("Error valid jwt token")));

        final StandardErrorDto errorItem = new StandardErrorDto(
                "VG-ENCRYPTION-JWT",
                "Error valid jwt token");

        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        assertNotNull(response);
        assertEquals(expect.getErrors().getFirst().getCode(), response.getErrors().getFirst().getCode());
    }

    @Test
    void test_getComponentRecover_EncryptionExceptionText() {

        final String token = "ey";
        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.just(JwtUtilDto.builder()
                .value("TEXT")
                .origen("Externo").build()
        ));
        Mockito.when(this.encryptionUtilMock.decrypt(anyString())).thenReturn(Mono.error(new EncryptionException.RecoverEncryptionException("Error decrypt recover token")));

        final StandardErrorDto errorItem = new StandardErrorDto(
                "VG-ENCRYPTION-RECOVER",
                "Error decrypt recover token");

        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        assertNotNull(response);
        assertEquals(expect.getErrors().getFirst().getCode(), response.getErrors().getFirst().getCode());
    }

    @Test
    void test_getComponentRecover_ErrorObjectMapper() {

        final String token = "ey";
        final RecoverComponentDto recoverComponentDto = new RecoverComponentDto();
        recoverComponentDto.setToken(token);

        final String dataEncrypted = "shjajsja";
        final String dataDecrypted = "{\"cotiS$\",\"email\":\"Agente.Sura@segurossura.com.mx\",\"oficinaId\":1,\"ramoId\":\"902\"}";

        Mockito.when(this.jwtUtilMock.validateJwt(anyString())).thenReturn(Mono.just(JwtUtilDto.builder()
                .value(dataEncrypted)
                .origen("Externo").build()
        ));
        Mockito.when(this.encryptionUtilMock.decrypt(anyString())).thenReturn(Mono.just(dataDecrypted));

        final StandardErrorDto errorItem = new StandardErrorDto(
                "VG-ENCRYPTION-RECOVER",
                "Error decrypt recover token");

        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH + "/get")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(recoverComponentDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(expect.getErrors().getFirst().getCode(), response.getErrors().getFirst().getCode());
    }

    private InsuranceDataDto createInsuranceDataDto() {
        final DiffAdminOpDto diffAdminOpDto = new DiffAdminOpDto(30, "comparator");
        final InsuredDto insuredDto = new InsuredDto(7, 1000, diffAdminOpDto);

        final AgeDto ageDto = new AgeDto(12, 65, 24, 60, null);

        final YearLimitDto yearLimitDtoMin = new YearLimitDto(15, "YEAR");
        final YearLimitDto yearLimitDtoMax = new YearLimitDto(69, "YEAR");
        final AgeLimitDto ageLimitDtoAcceptable = new AgeLimitDto(yearLimitDtoMin, yearLimitDtoMax);

        final YearLimitDto minRenovation = new YearLimitDto(79, "YEAR");
        final YearLimitDto maxRenovation = new YearLimitDto(79, "YEAR");
        final AgeLimitDto ageLimitDtoRenovation = new AgeLimitDto(minRenovation, maxRenovation);

        final YearLimitDto minCancellation = new YearLimitDto(80, "YEAR");
        final YearLimitDto maxCancellation = new YearLimitDto(80, "YEAR");
        final AgeLimitDto ageLimitDtoCancellation = new AgeLimitDto(minCancellation, maxCancellation);

        final SumDto sumDtoMin = new SumDto(new BigDecimal(500000), null, "formula", "formula");
        final SumDto sumDtoMax = new SumDto(new BigDecimal(500000), null, "formula", "formula");
        final InsuredSumLimitDto insuredSumLimitDto = new InsuredSumLimitDto(sumDtoMin, sumDtoMax);

        final LimitBasic limitBasic = new LimitBasic(10, 50);
        final ComparatorDto comparatortStandardDeviation = new ComparatorDto(10, "*");
        final ComparatorDto comparatorActuarialAge = new ComparatorDto(15, "*");
        final ComparatorDto comparatorquotient = new ComparatorDto(20, "*");
        final SamiDto sami = new SamiDto(10, 50, 20, 50L);

        final InsuredValidationDto insuredValidationDto = new InsuredValidationDto(null, "T", "Titular", ageLimitDtoAcceptable, ageLimitDtoRenovation,
                ageLimitDtoCancellation, List.of(insuredSumLimitDto));

        final CoverageDetailDto coverageDetailDto = new CoverageDetailDto("FALLECIMIENTO", "00001", "Fallecimiento",
                "BASICA", null, true, List.of(insuredValidationDto), null, null, false);

        final CoverageCatalogDto coverageCatalogList = new CoverageCatalogDto("TRADICIONAL", "Modalidad Tradicional", insuredDto, ageDto,
                List.of(coverageDetailDto), 10, limitBasic, List.of(sami),  null,null, comparatorActuarialAge, comparatortStandardDeviation, comparatorquotient, 40
        );

        final CatalogDataDto catalogDataDto = new CatalogDataDto("VIDA_GRUPO", "Catálogo de Coberturas Vida Grupo", List.of(coverageCatalogList));

        return new InsuranceDataDto(List.of(catalogDataDto));
    }

    private InsuranceDataDto createInsuranceAgeDataDto() {
        final DiffAdminOpDto diffAdminOpDto = new DiffAdminOpDto(30, "comparator");
        final InsuredDto insuredDto = new InsuredDto(7, 1000, diffAdminOpDto);

        final AgeDto ageDto = new AgeDto(12, 65, 30, 30, null);

        final YearLimitDto yearLimitDtoMin = new YearLimitDto(15, "YEAR");
        final YearLimitDto yearLimitDtoMax = new YearLimitDto(69, "YEAR");
        final AgeLimitDto ageLimitDtoAcceptable = new AgeLimitDto(yearLimitDtoMin, yearLimitDtoMax);

        final YearLimitDto minRenovation = new YearLimitDto(79, "YEAR");
        final YearLimitDto maxRenovation = new YearLimitDto(79, "YEAR");
        final AgeLimitDto ageLimitDtoRenovation = new AgeLimitDto(minRenovation, maxRenovation);

        final YearLimitDto minCancellation = new YearLimitDto(80, "YEAR");
        final YearLimitDto maxCancellation = new YearLimitDto(80, "YEAR");
        final AgeLimitDto ageLimitDtoCancellation = new AgeLimitDto(minCancellation, maxCancellation);

        final SumDto sumDtoMin = new SumDto(new BigDecimal(500000), null, "formula", "formula");
        final SumDto sumDtoMax = new SumDto(new BigDecimal(500000), null, "formula", "formula");
        final InsuredSumLimitDto insuredSumLimitDto = new InsuredSumLimitDto(sumDtoMin, sumDtoMax);

        final LimitBasic limitBasic = new LimitBasic(10, 50);
        final ComparatorDto comparatortStandardDeviation = new ComparatorDto(10, "*");
        final ComparatorDto comparatorActuarialAge = new ComparatorDto(15, "*");
        final ComparatorDto comparatorquotient = new ComparatorDto(20, "*");
        final SamiDto sami = new SamiDto(10, 50, 20, 50L);

        final InsuredValidationDto insuredValidationDto = new InsuredValidationDto(null, "T", "Titular", ageLimitDtoAcceptable, ageLimitDtoRenovation,
                ageLimitDtoCancellation, List.of(insuredSumLimitDto));

        final CoverageDetailDto coverageDetailDto = new CoverageDetailDto("FALLECIMIENTO", "00001", "Fallecimiento",
                "BASICA", null, true, List.of(insuredValidationDto), null, null, false);

        final CoverageCatalogDto coverageCatalogList = new CoverageCatalogDto("TRADICIONAL", "Modalidad Tradicional", insuredDto, ageDto,
                List.of(coverageDetailDto), 10, limitBasic, List.of(sami),  null,null, comparatorActuarialAge, comparatortStandardDeviation, comparatorquotient, 40
        );

        final CatalogDataDto catalogDataDto = new CatalogDataDto("VIDA_GRUPO", "Catálogo de Coberturas Vida Grupo", List.of(coverageCatalogList));

        return new InsuranceDataDto(List.of(catalogDataDto));
    }

}
