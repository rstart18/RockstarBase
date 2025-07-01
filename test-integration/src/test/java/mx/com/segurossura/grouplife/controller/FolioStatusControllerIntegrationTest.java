package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSum;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission.CommissionDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.openapi.model.CreateClientResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class FolioStatusControllerIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/folio-status";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setup() {
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        doReturn(this.requestBodySpec)
                .when(this.requestBodyUriSpec)
                .uri(this.uriCaptor.capture());
        when(this.requestBodySpec.bodyValue(any())).thenAnswer(invocation -> this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.bodyToMono(CommissionDataResponseDto.class))
                .thenReturn(Mono.just(TestFixtures.createCommissionDataResponseDto()));
    }

    @Test
    void test_getQuotation_success() {

        createFolio();

        final FolioStatus folioStatus = createFolioStatusRequest();

        final CreateClientResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(folioStatus))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreateClientResponseDto.class)
                .returnResult().getResponseBody();

        assertNotNull(actualResponse);
    }

    private void createFolio() {

        FolioNumber folioNumber = new FolioNumber("19990");
        QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        CatalogItem catalogItem = new CatalogItem("001", "GIRO");
        Company company = new Company("acme", catalogItem, 30, 100, 23, new BigDecimal(49), new BigDecimal(25));
        DiffAdminOp diffAdminOp = new DiffAdminOp(30, "lt");
        Insured insured = new Insured(7, 1000, diffAdminOp);
        Age age = new Age(18, 69, 18, 49, null);
        LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);

        RecordFolio recordFolio = RecordFolio.builder()
                .userId("OPS$LALOZANO")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Luis Antonio")
                .email("Luis.Lozano@segurossura.com.mx")
                .officeId("1")
                .officeDescription("OFICINA MEXICO, D.F.")
                .agentId("000001")
                .agentName("AGENTE DIRECTO")
                .promoterName("Alejandro Perea Mejia (METROPOLITANA)")
                .promoterId("967")
                .build();

        InsuredSum.InfoDoc infoDoc1 = new InsuredSum.InfoDoc();
        infoDoc1.setTypeCoverage("ADMINISTRATIVOS");
        infoDoc1.setInsureSum(new BigDecimal(500000));

        CoverageDetail coverageDetail1 = CoverageDetail.builder()
                .coverageKey("FALLECIMIENTO")
                .code("9842")
                .description("Fallecimiento.")
                .typeCoverage("BASICA")
                .mandatory(true)
                .insuredValidations(null)
                .insuredSumCoverages(List.of(infoDoc1))
                .build();

        CoverageDetail coverageDetail2 = CoverageDetail.builder()
                .coverageKey("FALLECIMIENTO")
                .code("9842")
                .description("Fallecimiento.")
                .typeCoverage("BASICA")
                .mandatory(true)
                .insuredValidations(null)
                .insuredSumCoverages(List.of(infoDoc1))
                .build();

        GroupVg groupVg1 = GroupVg.builder()
                .groupNumber(1)
                .name("nameGroup 1")
                .groupType("SA_FIJA")
                .numAdministrativeInsured(2)
                .numOperationalInsured(1)
                .administrativeInsuredSum(new BigDecimal(1440000))
                .operationalInsuredSum(new BigDecimal(1000000))
                .salaryMonth(12)
                .averageMonthlySalary(Double.parseDouble("12"))
                .coverages(List.of(coverageDetail1))
                .build();

        GroupVg groupVg2 = GroupVg.builder()
                .groupNumber(2)
                .name("nameGroup 2")
                .groupType("MESES_SUELDO")
                .numAdministrativeInsured(2)
                .numOperationalInsured(1)
                .administrativeInsuredSum(new BigDecimal(1440000))
                .operationalInsuredSum(new BigDecimal(1000000))
                .salaryMonth(12)
                .averageMonthlySalary(Double.parseDouble("12"))
                .coverages(List.of(coverageDetail2))
                .build();

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setGroups(List.of(groupVg1, groupVg2));
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setPlan(null);
        folioRecord.setCompany(company);
        folioRecord.setCreatedAt(LocalDateTime.parse("2023-11-09T11:50:58.389"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-11-09T15:07:46.76"));
        folioRecord.setStatus("Abierto");
        folioRecord.setQuotationDetails(quotationDetails);

        this.reactiveMongoTemplate.insert(folioRecord).block();
    }

    private FolioStatus createFolioStatusRequest() {
        final FolioStatus.Payment payment = new FolioStatus.Payment("approved");
        return new FolioStatus("19990", payment);
    }

}
