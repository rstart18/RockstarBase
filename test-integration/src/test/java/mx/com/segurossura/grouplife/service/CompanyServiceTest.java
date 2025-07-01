package mx.com.segurossura.grouplife.service;

import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.application.service.CatalogService;
import mx.com.segurossura.grouplife.application.service.CommissionService;
import mx.com.segurossura.grouplife.application.service.CompanyService;
import mx.com.segurossura.grouplife.application.validator.CompanyValidator;
import mx.com.segurossura.grouplife.domain.model.comission.CommissionAgent;
import mx.com.segurossura.grouplife.domain.model.company.CompanyModality;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CompanyServiceTest {

    @Mock
    private DbPort dbPort;

    @Mock
    private CompanyValidator companyValidator;

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CatalogService catalogService;

    @Mock
    private CommissionService commissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_processCoverageCatalogTraditionalWithoutPlan() {
        // Datos de prueba
        FolioRecord folioRecord = FolioRecord.builder()
                .modality(Modality.TRADITIONAL)
                .folio(Folio.builder().numberFolio(1234L).build())
                .subgroupId("0547000001")
                .status("Abierto")
                .build();

        CoverageDetail coverageDetail = CoverageDetail.builder()
                .code("code")
                .typeCoverage("type")
                .coverageKey("key")
                .insuredValidations(List.of())
                .mandatory(false)
                .build();
        List<CoverageDetail> coverages = List.of(coverageDetail);

        final CoverageCatalog coverageCatalog = CoverageCatalog.builder()
                .coverages(coverages)
                .insured(new Insured(0, 100, null))
                .age(new Age(0, 100, 25, 50, null))
                .modalityKey("TRADICIONAL")
                .maxGroups(10)
                .build();
        final List<CoverageCatalog> coverageCatalogList = List.of(coverageCatalog);

        final CompanyModality companyModality = new CompanyModality(
                new Company("TEST COMPANY",
                        CatalogItem.builder().key("01").value("GIRO").build(),
                        40,
                        100,
                        20,
                        new BigDecimal(150000),
                        new BigDecimal(120000)),
                null,
                Modality.TRADITIONAL
        );

        final ModalityValidation modalityValidation = new ModalityValidation(
                new Insured(7, 1000, null),
                new Age(0, 99, 23, 64, null),
                new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000)),
                10, null, null, null,null, null, null, null
        );

        final CommissionAgent commissionAgent = new CommissionAgent(
                0.2D, 0.01D
        );

        // Simulando respuestas de los mocks
        when(this.catalogPort.getCoverages()).thenReturn(Mono.just(coverageCatalogList));
        when(this.dbPort.createCompany(any())).thenReturn(Mono.just(FolioRecordResponse.builder().build()));
        when(this.companyValidator.canEdit(folioRecord, companyModality)).thenReturn(Mono.empty());
        when(this.companyValidator.validate(companyModality, coverageCatalog)).thenReturn(Mono.just(modalityValidation));
        when(this.catalogService.getCoverageCatalogModality("TRADICIONAL")).thenReturn(Mono.just(coverageCatalog));
        when(this.dbPort.findFolioRecord("folio123")).thenReturn(Mono.just(folioRecord));
        when(this.commissionService.getComissionsAgent(any())).thenReturn(Mono.just(commissionAgent));

        // Ejecución del método público que cubre el privado
        Mono<FolioRecordResponse> result = companyService.createCompany("folio123", companyModality);

        // Verificación del resultado
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void test_processCoverageCatalogTraditionalWithPlan() {
        // Datos de prueba
        FolioRecord folioRecord = FolioRecord.builder()
                .modality(Modality.TRADITIONAL)
                .folio(Folio.builder().numberFolio(1234L).build())
                .subgroupId("0547000001")
                .plan(new Plan("key", "name", List.of("001")))
                .company(Company.builder()
                        .name("ACME")
                        .numOperationalInsured(10)
                        .numAdministrativeInsured(30)
                        .administrativeInsuredSum(new BigDecimal(1000))
                        .operationalInsuredSum(new BigDecimal(1000))
                        .build())
                .status("Abierto")
                .build();

        CoverageDetail coverageDetail = CoverageDetail.builder()
                .code("001")
                .build();
        List<CoverageDetail> coverages = List.of(coverageDetail);

        CoverageCatalog coverageCatalog = CoverageCatalog.builder()
                .coverages(coverages)
                .modalityKey("TRADICIONAL")
                .insured(new Insured(0, 100, null))
                .age(new Age(0, 100, 25, 50, null))
                .maxGroups(10)
                .build();

        final CompanyModality companyModality = new CompanyModality(
                new Company("TEST COMPANY",
                        CatalogItem.builder().key("01").value("GIRO").build(),
                        40,
                        100,
                        20,
                        new BigDecimal(150000),
                        new BigDecimal(120000)),
                new Plan("1", "PLAN", List.of()),
                Modality.TRADITIONAL
        );

        final ModalityValidation modalityValidation = new ModalityValidation(
                new Insured(7, 1000, null),
                new Age(0, 99, 23, 64, null),
                new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000)),
                10, null, null, null,null, null, null, null
        );

        final CommissionAgent commissionAgent = new CommissionAgent(
                0.2D, 0.01D
        );

        // Simulando respuestas de los mocks
        when(this.catalogPort.getCoverages()).thenReturn(Mono.just(List.of(coverageCatalog)));
        when(this.dbPort.createCompany(any())).thenReturn(Mono.just(FolioRecordResponse.builder().build()));
        when(this.companyValidator.canEdit(folioRecord, companyModality)).thenReturn(Mono.empty());
        when(this.companyValidator.validate(companyModality, coverageCatalog)).thenReturn(Mono.just(modalityValidation));
        when(this.catalogService.getCoverageCatalogModality("TRADICIONAL")).thenReturn(Mono.just(coverageCatalog));
        when(this.dbPort.findFolioRecord("folio123")).thenReturn(Mono.just(folioRecord));
        when(this.commissionService.getComissionsAgent(any())).thenReturn(Mono.just(commissionAgent));

        // Ejecución del método público que cubre el privado
        Mono<FolioRecordResponse> result = companyService.createCompany("folio123", companyModality);

        // Verificación del resultado
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }
}

