package mx.com.segurossura.grouplife.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
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
import mx.com.segurossura.grouplife.domain.model.insured.AggregateInsuredGroup;
import mx.com.segurossura.grouplife.domain.model.insured.Insured;
import mx.com.segurossura.grouplife.domain.model.insured.InsuredGroup;
import mx.com.segurossura.grouplife.domain.model.issue.StatusFolio;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue.StatusFolioDataResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.AttributesDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.CoveragesDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.FolioRecordRequestPolicyDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.GroupsRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.QuotationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.when;

public class QuotationGatewayTest {

    @Mock
    private WebClient quotationWebClient;
    @InjectMocks
    private QuotationGateway quotationGateway;
    @Mock
    private QuotationMapper quotationMapper;
    @Mock
    private CircuitBreaker circuitBreaker;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.quotationWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestHeadersSpec);
        when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);

        // Crea un Retry falso que no haga nada (maxAttempts = 1)
        Retry retry = Retry.of("testRetry", RetryConfig.custom().maxAttempts(1).build());
        ReflectionTestUtils.setField(quotationGateway, "retry", retry);
    }

    @Test
    void verifyIssue_Success() {

        final StatusFolioDataResponse statusFolioDataResponse = createStatusFolioDataResponse();
        final StatusFolio statusFolio = createStatusFolio();

        // Configura el mock de CircuitBreaker para permitir la llamada
        when(circuitBreaker.tryAcquirePermission()).thenReturn(true);

        // Configura el mock de WebClient para devolver una respuesta exitosa
        when(responseSpec.bodyToMono(StatusFolioDataResponse.class)).thenReturn(Mono.just(statusFolioDataResponse));

        when(this.quotationMapper.statusFolioResponseToModel(statusFolioDataResponse.data())).thenReturn(statusFolio);

        // Verificación
        StepVerifier.create(quotationGateway.verifyIssue(102L))
                .expectNextMatches(item -> item.policy().policyNumber() == 123)
                .verifyComplete();
    }

    private StatusFolioDataResponse createStatusFolioDataResponse() {
        final StatusFolioDataResponse.StatusFolio.Policy policy = new StatusFolioDataResponse.StatusFolio.Policy(1, 902, "W", 123L);
        final StatusFolioDataResponse.StatusFolio.Costs costs = new StatusFolioDataResponse.StatusFolio.Costs(123.45, 123.45, 123.45,
                123.45, 123.45, 123.45, 123.45,
                123.45, 123.45);
        final StatusFolioDataResponse.StatusFolio statusFolio = new StatusFolioDataResponse.StatusFolio(policy, costs, "1", "null", "102", LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now(), "COMPLETED", "OK");

        return new StatusFolioDataResponse(statusFolio);
    }

    private StatusFolio createStatusFolio() {
        final StatusFolio.Policy policy = new StatusFolio.Policy(1, 902, "W", 123L);
        final StatusFolio.Costs costs = new StatusFolio.Costs(123.45, 123.45, 123.45,
                123.45, 123.45, 123.45, 123.45,
                123.45, 123.45);
        return new StatusFolio(policy, costs, "1", "null", "102", LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now(), "COMPLETED", "OK");
    }

    private FolioRecord createFolioRecord() {

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

        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, List.of(sami), null,null, new Comparator(1, "nn"),
                new Comparator(1, "nn"), new Comparator(1, "nn"), 35);

        final Quote quote = new Quote(34654L, 1D, 902D, "W", 2343D, 3435.65,
                3435.65, 3435.65, 3435.65, 3435.65, 3435.65,
                3435.65, 3435.65, 3435.65);

        final Policy policy = new Policy("MXN", 3, 12, 123L, 1, 902, "W",
                2343L, 34654L, false, false, quote, "PENDING", "nn", false,
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

        return new FolioRecord(new Folio(97L, "nn", "nn", "nn"), "123", "123", "1", "2", "123", "company", "juan@gmail.com",
                "123", "1", "00001", "jose", "jose", "123", quotationDetails,
                modalityValidation, new Plan("key", "name", List.of("001")), costs, Modality.TRADITIONAL, company, client, LocalDateTime.now(), LocalDateTime.now(), List.of(groupVg),
                "Abierto", "por emitir", List.of(policy), new BigDecimal(500000));
    }

    private Policy createPolicy() {
        // Formato de fecha del JSON
        final String dateString = "2025-02-02T22:13:51.289Z";
        final LocalDateTime createdAt = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);

        // Crear instancia de Quote
        final Quote quote = new Quote(
                157L,
                1D,
                902D,
                "W",
                3788D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D,
                1005018D
        );

        return Policy.builder()
                .currency("MXN")
                .formPayment(8)
                .periodicity(12)
                .personCode(51975836L)
                .officeId(quote.officeId().intValue())
                .productId(quote.productId().intValue())
                .status(quote.policyState())
                .policyNumber(quote.policyNumber().longValue())
                .folioFromPartner(quote.folio())
                .statusInsureds(true)
                .statusClient(true)
                .createdAt(createdAt)
                .requestId("14")
                .costs(quote)
                .build();
    }

    private AggregateInsuredGroup createAggregateInsuredGroup() {

        final Insured insured = new Insured(1, null, null, null, null, null, "jode", "antonio", "perez", "gomez",
                LocalDate.now().minusYears(20), "M", 5345435D, "Administrativo");

        final InsuredGroup insuredGroup = new InsuredGroup(1, "nn", "544565.456", List.of(insured));

        return new AggregateInsuredGroup("9700", List.of(insuredGroup), 35D, 35D, 35,
                35D, 345435D, 5435D, 35D
        );
    }

    public FolioRecordRequestPolicyDto createFolioRecordRequestPolicyDto() {

        final CoveragesDto coveragesDto = new CoveragesDto("9842", 123, new BigDecimal("3456546"), new BigDecimal("3456546"));
        final GroupsRequestDto groupsRequestDto = new GroupsRequestDto("1", "nn", "FIJA", 100000, 12,
                10, 1, List.of(coveragesDto));

        final AttributesDto attributesDto = new AttributesDto("OTVALOR05", "1");

        FolioRecordRequestPolicyDto.FolioRecordRequestPolicyDtoBuilder folioRecordRequestPolicyDto = FolioRecordRequestPolicyDto.builder();
        folioRecordRequestPolicyDto.officeId(1);
        folioRecordRequestPolicyDto.productId(902);
        folioRecordRequestPolicyDto.folio("9700");
        folioRecordRequestPolicyDto.companyName("company");
        folioRecordRequestPolicyDto.modality("TRADICIONAL");
        folioRecordRequestPolicyDto.agentCode("000001");
        folioRecordRequestPolicyDto.effectiveDate(LocalDate.now());
        folioRecordRequestPolicyDto.groups(List.of(groupsRequestDto));
        folioRecordRequestPolicyDto.attributes(List.of(attributesDto));

        return folioRecordRequestPolicyDto.build();
    }

}
