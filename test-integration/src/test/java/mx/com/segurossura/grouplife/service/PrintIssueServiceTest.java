package mx.com.segurossura.grouplife.service;

import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.application.port.PrintPort;
import mx.com.segurossura.grouplife.application.service.PrintIssueService;
import mx.com.segurossura.grouplife.application.service.ServiceUtils;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.Ages;
import mx.com.segurossura.grouplife.domain.model.coverage.Comparator;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.DefaultValue;
import mx.com.segurossura.grouplife.domain.model.coverage.Dependencies;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Display;
import mx.com.segurossura.grouplife.domain.model.coverage.Grouped;
import mx.com.segurossura.grouplife.domain.model.coverage.Imc;
import mx.com.segurossura.grouplife.domain.model.coverage.ImcValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSum;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.Sami;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.domain.model.enums.AdministrationType;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrintIssueServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PrintIssueServiceTest.class);
    private final byte[] bytes = new byte[10];
    private PrintIssueService printIssueService;
    private ServiceUtils serviceUtils;
    private PrintPort printPort;
    private DbPort dbPort;
    private CatalogPort catalogPort;

    @BeforeEach
    void setUp() {
        this.printPort = mock(PrintPort.class);
        this.dbPort = mock(DbPort.class);
        this.catalogPort = mock(CatalogPort.class);

        this.serviceUtils = new ServiceUtils(this.dbPort, this.catalogPort);

        this.printIssueService = new PrintIssueService(
                this.printPort,
                this.serviceUtils
        );
    }

    @Test
    void test_verifyFoliosPendingIssue() {

        FolioRecord folioRecord = createFolioRecord(200);

        // Simulando respuestas de los mocks
        when(this.printPort.getPDFConsolidated(folioRecord.policy().getLast())).thenReturn(Mono.just(bytes));

        when(this.dbPort.findFolioRecord("200")).thenReturn(Mono.just(folioRecord));
        when(this.serviceUtils.getFolioRecord("200")).thenReturn(Mono.just(folioRecord));

        // Verificación del resultado
        StepVerifier.create(this.printIssueService.getPDF("200"))
                .expectNext(bytes)
                .verifyComplete();
    }

    private FolioRecord createFolioRecord(final long folio) {

        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);

        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), new Dependencies("nn", "nn"), "nn", "nn"), new Sum(new BigDecimal(500000),
                new Dependencies("nn", "nn"), "nn", "nn"));

        final InsuredValidation insuredValidation = new InsuredValidation(null, "nn", "nn", acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));

        final DefaultValue defaultValue = new DefaultValue(new BigDecimal(500000), new Dependencies("nn", "nn"),
                "nn", "nn");

        final InsuredSum.InfoDoc info = new InsuredSum.InfoDoc();
        info.setTypeCoverage("nn");
        info.setInsureSum(new BigDecimal(500000));

        final Grouped grouped = new Grouped("nn", "nn", List.of("nn"));

        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, defaultValue, List.of(insuredValidation), List.of(info), new Display("nn", "nn"), grouped, true);

        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, 456546D,
                salaries, List.of(coverageDetail));

        final QuotationDetails quotationDetails = new QuotationDetails(0.10, 0.10,
                0.10, 0.10, LocalDate.now(), LocalDate.now().plusYears(1),
                "nn", AdministrationType.AUTOADMINISTRADA);

        final Company company = new Company("nn", new CatalogItem("valor", "valor"), 35, 19,
                3, new BigDecimal(500000), new BigDecimal(500000));

        final mx.com.segurossura.grouplife.domain.model.coverage.Insured insured = new mx.com.segurossura.grouplife.domain.model.coverage.Insured(12, 12,
                new DiffAdminOp(1, "nn"));
        final Age age = new Age(12, 12, 12, 12, 35);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final Sami sami = new Sami(12, 30, 15, 35L);

        final Ages ages = new Ages(35, 45, 35, 45, 45, "nn");
        final Imc imc = new Imc(35, 45);
        final ImcValidation imcValidation = new ImcValidation(imc, List.of(ages));

        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, List.of(sami), new BigDecimal(500000), List.of(imcValidation),
                new Comparator(1, "nn"),
                new Comparator(1, "nn"), new Comparator(1, "nn"), 35);

        final Quote quote = new Quote(34654L, 1D, 902D, "W", 2343D, 3435.65,
                3435.65, 3435.65, 3435.65, 3435.65, 3435.65,
                3435.65, 3435.65, 3435.65);

        final Policy policy = new Policy("MXN", 8, 12, 123L, 1, 902, "W",
                2343L, folio, true, true, quote, "PENDING", "nn", true,
                LocalDateTime.now(), "9", new Policy.Issue(1, 902, "M", 2344L), "url", false, true, null);

        GeneralInfo general = GeneralInfo.builder()
                .typeLegalId(2)
                .rfc("EMP241106MG4")
                .businessName("empresa")
                .constitutionDate(LocalDate.parse("2024-11-06"))
                .legalRepresentativeName("Juan")
                .birthdate(LocalDate.parse("1985-11-06"))
                .email("juan.perez@email.com")
                .phoneNumber("5551234567").build();

        final AddressClient addressClient = AddressClient.builder()
                .streetName("Main St")
                .streetNumberExt("123")
                .internalDepartmentNumber("B")
                .zipCode("12345")
                .stateId("Jalisco")
                .municipality("Guadalajara")
                .colonyId("Downtown").build();

        final Client client = Client.builder()
                .general(general)
                .address(addressClient)
                .invoicing(general)
                .billingAddress(addressClient).build();

        final Costs costs = new Costs(new BigDecimal(500000), new BigDecimal(500000), new BigDecimal(500000),
                new BigDecimal(500000), new BigDecimal(500000));

        return new FolioRecord(new Folio(folio, "nn", "nn", "nn"), "123", "123", "1", "2", "123", "company", "juan@gmail.com",
                "123", "1", "00001", "jose", "jose", "123", quotationDetails,
                modalityValidation, new Plan("key", "name", List.of("001")), costs, Modality.TRADITIONAL, company, client, LocalDateTime.now(), LocalDateTime.now(), List.of(groupVg),
                "Abierto", "por emitir", List.of(policy), new BigDecimal(500000));

        //this.reactiveMongoTemplate.insert(folioRecord).block();
        //return folioRecord;
    }

}
