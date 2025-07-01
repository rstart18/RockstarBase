package mx.com.segurossura.grouplife.service;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.application.port.MailSendGridPort;
import mx.com.segurossura.grouplife.application.port.PaymentLinkPort;
import mx.com.segurossura.grouplife.application.port.PrintPort;
import mx.com.segurossura.grouplife.application.port.QuotationPort;
import mx.com.segurossura.grouplife.application.service.GetPaymentLinkService;
import mx.com.segurossura.grouplife.application.service.MailSendGridService;
import mx.com.segurossura.grouplife.application.service.PrintIssueService;
import mx.com.segurossura.grouplife.application.service.ServiceUtils;
import mx.com.segurossura.grouplife.application.service.VerifyIssueService;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.Comparator;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.DefaultValue;
import mx.com.segurossura.grouplife.domain.model.coverage.Dependencies;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Display;
import mx.com.segurossura.grouplife.domain.model.coverage.Grouped;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSum;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.Sami;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.domain.model.enums.AdministrationType;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.domain.model.issue.StatusFolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
public class VerifyIssueServiceTest extends BaseIT {

    private static final Logger log = LoggerFactory.getLogger(VerifyIssueServiceTest.class);

    private QuotationPort quotationPort;
    private PrintPort printPort;
    private VerifyIssueService verifyIssueService;
    private GetPaymentLinkService getPaymentLinkService;
    private PaymentLinkPort paymentLinkPort;
    private PrintIssueService printIssueService;
    private MailSendGridService mailSendGridService;
    private MailSendGridPort mailSendGridPort;
    private DbPort dbPort;
    private CatalogPort catalogPort;
    private ServiceUtils serviceUtils;

    @Mock
    private DbPort dbPort2;
    @Mock
    private QuotationPort quotationPort2;
    @InjectMocks
    private VerifyIssueService verifyIssueService2;

    @BeforeEach
    void setUp() {
        this.mailSendGridPort = mock(MailSendGridPort.class);
        this.quotationPort = mock(QuotationPort.class);
        this.paymentLinkPort = mock(PaymentLinkPort.class);
        this.dbPort = mock(DbPort.class);
        this.printPort = mock(PrintPort.class);
        this.catalogPort = mock(CatalogPort.class);
        this.serviceUtils = new ServiceUtils(this.dbPort, this.catalogPort);

        this.printIssueService = new PrintIssueService(printPort, serviceUtils);
        this.mailSendGridService = new MailSendGridService(
                this.mailSendGridPort,
                this.serviceUtils
        );
        this.getPaymentLinkService = new GetPaymentLinkService(
                this.serviceUtils,
                this.paymentLinkPort,
                this.dbPort
        );
        this.verifyIssueService = new VerifyIssueService(
                this.dbPort,
                this.quotationPort,
                this.getPaymentLinkService,
                this.printIssueService,
                this.mailSendGridService
        );
    }

    @Test
    void test_verifyFoliosPendingIssue() {

        FolioRecord folioRecord = createFolioRecord(100);

        StatusFolio statusFolio = createStatusFolio("100", "PENDING");

        // Simulando respuestas de los mocks
        when(this.dbPort.getFolioToStatusIssue()).thenReturn(Flux.just(folioRecord));
        when(this.quotationPort.verifyIssue(folioRecord.folio().numberFolio())).thenReturn(Mono.just(statusFolio));

        // Verificación del resultado
        StepVerifier.create(verifyIssueService.verifyFoliosPendingIssue())
                .verifyComplete();
    }

    @Test
    void test_getFolioToStatusIssue() {

        FolioRecord folioRecord = createFolioRecordInsert(111);

        StatusFolio statusFolio = createStatusFolio("111", "PENDING");

        // Simulando respuestas de los mocks
        when(this.dbPort2.getFolioToStatusIssue()).thenReturn(Flux.just(folioRecord));
        when(this.quotationPort2.verifyIssue(folioRecord.folio().numberFolio())).thenReturn(Mono.just(statusFolio));

        // Verificación del resultado
        StepVerifier.create(verifyIssueService2.verifyFoliosPendingIssue())
                .verifyComplete();
    }

    @Test
    void test_verifyFoliosCompletedIssue() {

        FolioRecord folioRecord = createFolioRecord(101);

        PaymentLinkResponseAggregate paymentLinkResponseAggregate = createPaymentLinkResponseAggregate();

        StatusFolio statusFolio = createStatusFolio("101", "COMPLETED");

        //when(this.dbPort.saveFolioQuote(folioRecord)).thenReturn(Mono.just(folioRecord));
        doReturn(Mono.just(folioRecord)).when(dbPort).saveFolioQuote(Mockito.any(FolioRecord.class));

        when(this.dbPort.getFolioToStatusIssue()).thenReturn(Flux.just(folioRecord));
        when(this.quotationPort.verifyIssue(folioRecord.folio().numberFolio())).thenReturn(Mono.just(statusFolio));

        when(this.dbPort.findFolioRecord("101")).thenReturn(Mono.just(folioRecord));
        when(this.serviceUtils.getFolioRecord("101")).thenReturn(Mono.just(folioRecord));

        //when(this.paymentLinkPort.getPaymentLink(folioRecord)).thenReturn(Mono.just(paymentLinkResponseAggregate));
        doReturn(Mono.just(paymentLinkResponseAggregate)).when(paymentLinkPort).getPaymentLink(folioRecord);

        //when(this.mailSendGridPort.sendNotificationMail("juan@gmail.com", "101", true)).thenReturn(Mono.just("OK"));
        doReturn(Mono.just("OK")).when(mailSendGridPort).sendNotificationMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyBoolean(), eq(folioRecord.modality()));

        //when(this.getPaymentLinkService.getPaymentLinkService(101L)).thenReturn(Mono.just(paymentLinkResponseAggregate));
        //when(this.mailSendGridService.sendNotificationMail(folioRecord, true)).thenReturn(Mono.just("OK"));

        // Verificación del resultado
        StepVerifier.create(verifyIssueService.verifyFoliosPendingIssue())
                .verifyComplete();
    }

    @Test
    void test_verifyFoliosFailedIssue() {

        FolioRecord folioRecord = createFolioRecord(102);

        PaymentLinkResponseAggregate paymentLinkResponseAggregate = createPaymentLinkResponseAggregate();

        StatusFolio statusFolio = createStatusFolio("102", "FAILED");

        //when(this.dbPort.saveFolioQuote(folioRecord)).thenReturn(Mono.just(folioRecord));
        doReturn(Mono.just(folioRecord)).when(dbPort).saveFolioQuote(Mockito.any(FolioRecord.class));

        when(this.dbPort.getFolioToStatusIssue()).thenReturn(Flux.just(folioRecord));
        when(this.quotationPort.verifyIssue(folioRecord.folio().numberFolio())).thenReturn(Mono.just(statusFolio));

        when(this.dbPort.findFolioRecord("102")).thenReturn(Mono.just(folioRecord));
        when(this.serviceUtils.getFolioRecord("102")).thenReturn(Mono.just(folioRecord));

        //when(this.paymentLinkPort.getPaymentLink(folioRecord)).thenReturn(Mono.just(paymentLinkResponseAggregate));
        doReturn(Mono.just(paymentLinkResponseAggregate)).when(paymentLinkPort).getPaymentLink(Mockito.any(FolioRecord.class));

        //when(this.mailSendGridPort.sendNotificationMail("juan@gmail.com", "101", true)).thenReturn(Mono.just("OK"));
        doReturn(Mono.just("OK")).when(mailSendGridPort).sendNotificationMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyBoolean(), eq(folioRecord.modality()));

        //when(this.getPaymentLinkService.getPaymentLinkService(101L)).thenReturn(Mono.just(paymentLinkResponseAggregate));
        //when(this.mailSendGridService.sendNotificationMail(folioRecord, true)).thenReturn(Mono.just("OK"));

        // Verificación del resultado
        StepVerifier.create(verifyIssueService.verifyFoliosPendingIssue())
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

        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, List.of(sami), null, null, new Comparator(1, "nn"),
                new Comparator(1, "nn"), new Comparator(1, "nn"), 35);

        final Quote quote = new Quote(34654L, 1D, 902D, "W", 2343D, 3435.65,
                3435.65, 3435.65, 3435.65, 3435.65, 3435.65,
                3435.65, 3435.65, 3435.65);

        final Policy policy = new Policy("MXN", 8, 12, 123L, 1, 902, "W",
                2343L, folio, true, true, quote, "COMPLETED", "nn", true,
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

    private StatusFolio createStatusFolio(final String folio, final String status) {
        final StatusFolio.Policy policy = new StatusFolio.Policy(1, 902, "W", 123L);
        final StatusFolio.Costs costs = new StatusFolio.Costs(123.45, 123.45, 123.45,
                123.45, 123.45, 123.45, 123.45,
                123.45, 123.45);
        return new StatusFolio(policy, costs, "1", "null", folio, LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now(), status, "OK");
    }

    private PaymentLinkResponseAggregate createPaymentLinkResponseAggregate() {
        return new PaymentLinkResponseAggregate("url", LocalDate.now(), true);
    }

    private FolioRecord createFolioRecordInsert(final long folio) {

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

        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, List.of(sami), null, null, new Comparator(1, "nn"),
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

        FolioRecord folioRecord = new FolioRecord(new Folio(folio, "nn", "nn", "nn"), "123", "123", "1", "2", "123", "company", "juan@gmail.com",
                "123", "1", "00001", "jose", "jose", "123", quotationDetails,
                modalityValidation, new Plan("key", "name", List.of("001")), costs, Modality.TRADITIONAL, company, client, LocalDateTime.now(), LocalDateTime.now(), List.of(groupVg),
                "Abierto", "por emitir", List.of(policy), new BigDecimal(500000));

        this.reactiveMongoTemplate.insert(folioRecord).block();
        return folioRecord;
    }

}
