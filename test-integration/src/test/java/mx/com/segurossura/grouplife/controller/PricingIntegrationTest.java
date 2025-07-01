package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSum;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.insured.AggregateInsuredGroup;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission.CommissionDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.CoverageResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.FamilyResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.FinancialDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.InsuredVolunteerResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PaymentSurchargeResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PeriodicResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PlanDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PolicyHolderGroupResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PricingVolunteerResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.SumInsuredOccupationResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.SumNetPremiumResponseDto;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.FamilyMemberEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredGroup;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredGroupEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.QuestionnaireDataEntity;
import mx.com.segurossura.grouplife.openapi.model.ResponseQuotationLifeGroupV2Dto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

public class PricingIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/pricing/";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private final InsuredGroupMapper insuredGroupMapper = Mappers.getMapper(InsuredGroupMapper.class);
    @MockitoBean
    @Qualifier("pricingWebClient")
    private WebClient pricingWebClient;
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
        when(this.pricingWebClient.post()).thenReturn(this.requestBodyUriSpec);
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

        final PlanDataResponseDto planDataResponseDto = new PlanDataResponseDto(createPlanDataResponseDtoGtw());
        when(this.responseSpec.bodyToMono(PlanDataResponseDto.class)).thenReturn(Mono.just(planDataResponseDto));

        final AggregateInsuredGroup aggregateInsuredGroup = createAggregateInsuredGroup();
        when(this.responseSpec.bodyToMono(AggregateInsuredGroup.class)).thenReturn(Mono.just(aggregateInsuredGroup));

        createFolio();

        final ResponseQuotationLifeGroupV2Dto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH + "1999")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(ResponseQuotationLifeGroupV2Dto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(actualResponse);
    }

    @Test
    void test_getQuotation_success_volunteer() {

        final PricingVolunteerResponseDto pricingVolunteerResponseDto = createPlanDataResponseDtoGtwVolunteer();
        when(this.responseSpec.bodyToMono(PricingVolunteerResponseDto.class)).thenReturn(Mono.just(pricingVolunteerResponseDto));

        final AggregateInsuredGroup aggregateInsuredGroup = createAggregateInsuredGroupVolunteer();
        when(this.responseSpec.bodyToMono(AggregateInsuredGroup.class)).thenReturn(Mono.just(aggregateInsuredGroup));

        createFolioVolunteer();

        final ResponseQuotationLifeGroupV2Dto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH + "19991")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(ResponseQuotationLifeGroupV2Dto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(actualResponse);
    }

    private PlanDataResponseDto.PlanResponseListDto createPlanDataResponseDtoGtw() {

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto1 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(new BigDecimal("5000000"))
                .occupation("ADMINISTRATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto2 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(new BigDecimal("5000000"))
                .occupation("OPERATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto3 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(null)
                .occupation("ADMINISTRATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto4 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(null)
                .occupation("OPERATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        CoverageResponseDto coverageResponseDto1 = CoverageResponseDto.builder()
                .coverageKey("FALLECIMIENTO")
                .sumOccupation(List.of(sumInsuredOccupationResponseDto1, sumInsuredOccupationResponseDto2))
                .build();

        CoverageResponseDto coverageResponseDto2 = CoverageResponseDto.builder()
                .coverageKey("FALLECIMIENTO")
                .sumOccupation(List.of(sumInsuredOccupationResponseDto3, sumInsuredOccupationResponseDto4))
                .build();

        PolicyHolderGroupResponseDto policyHolderGroupResponseDto1 = PolicyHolderGroupResponseDto.builder()
                .coverages(List.of(coverageResponseDto1))
                .group(1)
                .rule("FIJA")
                .insuredSum(new BigDecimal("5000000"))
                .policyHolders(10)
                .basicRiskPremium(new BigDecimal("3065.5000"))
                .optionalRiskPremium(new BigDecimal("0.000"))
                .netPremium(new BigDecimal("4984.5528"))
                .administrativeSum(new BigDecimal("0.000"))
                .operativeSum(new BigDecimal("649.1870"))
                .salaryTotalNetPremiumAdmin(new BigDecimal("6499.180"))
                .salaryTotalNetPremiumOperative(new BigDecimal("6491.1870"))
                .totalNetPremiumGroupAdmin(new BigDecimal("6499.180"))
                .totalNetPremiumGroupOperative(new BigDecimal("6499.180"))
                .build();

        PolicyHolderGroupResponseDto policyHolderGroupResponseDto2 = PolicyHolderGroupResponseDto.builder()
                .coverages(List.of(coverageResponseDto2))
                .group(2)
                .rule("12")
                .insuredSum(new BigDecimal("5000000"))
                .policyHolders(10)
                .basicRiskPremium(new BigDecimal("3065.5000"))
                .optionalRiskPremium(new BigDecimal("0.000"))
                .netPremium(new BigDecimal("4984.5528"))
                .administrativeSum(new BigDecimal("0.000"))
                .operativeSum(new BigDecimal("649.1870"))
                .salaryTotalNetPremiumAdmin(new BigDecimal("6499.180"))
                .salaryTotalNetPremiumOperative(new BigDecimal("6491.1870"))
                .totalNetPremiumGroupAdmin(new BigDecimal("6499.180"))
                .totalNetPremiumGroupOperative(new BigDecimal("6499.180"))
                .build();

        PeriodicResponseDto periodicResponseDto = PeriodicResponseDto.builder()
                .netAnnualPremium(new BigDecimal("4984.5528"))
                .netSemiannualPremium(new BigDecimal("4984.5528"))
                .netQuarterlyPremium(new BigDecimal("4984.5528"))
                .netMonthlyPremium(new BigDecimal("4984.5528"))
                .annualPaymentSurcharge(new BigDecimal("4984.5528"))
                .semiannualPaymentSurcharge(new BigDecimal("4984.5528"))
                .quarterlyPaymentSurcharge(new BigDecimal("4984.5528"))
                .monthlyPaymentSurcharge(new BigDecimal("4984.5528"))
                .annualShippingFees(new BigDecimal("4984.5528"))
                .semiannualShippingFees(new BigDecimal("4984.5528"))
                .quarterlyShippingFees(new BigDecimal("4984.5528"))
                .monthlyShippingFees(new BigDecimal("4984.5528"))
                .totalAnnualPeriod(new BigDecimal("4984.5528"))
                .totalSemiannualPeriod(new BigDecimal("4984.5528"))
                .totalQuarterlyPeriod(new BigDecimal("4984.5528"))
                .totalMonthlyPeriod(new BigDecimal("4984.5528"))
                .build();

        FinancialDataResponseDto financialDataResponseDto = FinancialDataResponseDto.builder()
                .rightPolicy(new BigDecimal("4984.5528"))
                .fractionalSurcharge(new BigDecimal("4984.5528"))
                .vat(new BigDecimal("4984.5528"))
                .build();

        PlanDataResponseDto.PlanResponseDto planResponseDto = PlanDataResponseDto.PlanResponseDto.builder()
                .namePlan("BASICA")
                .totalNetPremium(new BigDecimal("64799.1870"))
                .totalPremium(new BigDecimal("65099.1870"))
                .periodic(periodicResponseDto)
                .financialData(financialDataResponseDto)
                .policyHoldersGroups(List.of(policyHolderGroupResponseDto1, policyHolderGroupResponseDto2))
                .build();

        return new PlanDataResponseDto.PlanResponseListDto(List.of(planResponseDto));
    }

    private void createFolio() {

        FolioNumber folioNumber = new FolioNumber("1999");
        QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        CatalogItem catalogItem = new CatalogItem("001", "GIRO");
        Company company = new Company("acme", catalogItem, 30, 100, 23, new BigDecimal(49), new BigDecimal(25));
        DiffAdminOp diffAdminOp = new DiffAdminOp(30, "lt");
        Insured insured = new Insured(7, 1000, diffAdminOp);
        Age age = new Age(18, 69, 18, 49, null);
        LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null, null, null, null, null, null);

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

    private AggregateInsuredGroup createAggregateInsuredGroup() {

        final FolioNumber folioNumber = new FolioNumber("1999");

        final InsuredEntity insuredEntity = new InsuredEntity(
                null,
                null,
                null,
                null,
                null,
                "Jose",
                "Pedro",
                "Perez",
                "Gomez",
                LocalDate.now(),
                "E",
                234234D,
                "Administrativos"
        );

        final InsuredGroup insuredGroup = new InsuredGroup();
        insuredGroup.setGroupNumber(1);
        insuredGroup.setName("grupo1");
        insuredGroup.setInsuredSumRule("34543.3456");
        insuredGroup.setInsureds(List.of(insuredEntity));

        final InsuredGroupEntity insuredGroupEntity = new InsuredGroupEntity();
        insuredGroupEntity.setId(folioNumber);
        insuredGroupEntity.setGroups(List.of(insuredGroup));
        insuredGroupEntity.setAverageAge(35D);
        insuredGroupEntity.setAdjustedAverageAge(35D);
        insuredGroupEntity.setActuarialAge(35);
        insuredGroupEntity.setDiffActuarialAverageAge(35D);
        insuredGroupEntity.setSami(35D);
        insuredGroupEntity.setStandardDeviation(35D);
        insuredGroupEntity.setQuotient(35D);

        return insuredGroupMapper.toModelGroupInsureds(insuredGroupEntity);
    }

    private PricingVolunteerResponseDto createPlanDataResponseDtoGtwVolunteer() {

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto1 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(new BigDecimal("5000000"))
                .occupation("ADMINISTRATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto2 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(new BigDecimal("5000000"))
                .occupation("OPERATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto3 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(null)
                .occupation("ADMINISTRATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        SumInsuredOccupationResponseDto sumInsuredOccupationResponseDto4 = SumInsuredOccupationResponseDto.builder()
                .insuredSum(null)
                .occupation("OPERATIVOS")
                .riskPremiumCoverage(new BigDecimal("0"))
                .build();

        CoverageResponseDto coverageResponseDto1 = CoverageResponseDto.builder()
                .coverageKey("FALLECIMIENTO")
                .sumOccupation(List.of(sumInsuredOccupationResponseDto1, sumInsuredOccupationResponseDto2))
                .build();

        CoverageResponseDto coverageResponseDto2 = CoverageResponseDto.builder()
                .coverageKey("FALLECIMIENTO")
                .sumOccupation(List.of(sumInsuredOccupationResponseDto3, sumInsuredOccupationResponseDto4))
                .build();

        PeriodicResponseDto periodicResponseDto = PeriodicResponseDto.builder()
                .netAnnualPremium(new BigDecimal("4984.5528"))
                .netSemiannualPremium(new BigDecimal("4984.5528"))
                .netQuarterlyPremium(new BigDecimal("4984.5528"))
                .netMonthlyPremium(new BigDecimal("4984.5528"))
                .annualPaymentSurcharge(new BigDecimal("4984.5528"))
                .semiannualPaymentSurcharge(new BigDecimal("4984.5528"))
                .quarterlyPaymentSurcharge(new BigDecimal("4984.5528"))
                .monthlyPaymentSurcharge(new BigDecimal("4984.5528"))
                .annualShippingFees(new BigDecimal("4984.5528"))
                .semiannualShippingFees(new BigDecimal("4984.5528"))
                .quarterlyShippingFees(new BigDecimal("4984.5528"))
                .monthlyShippingFees(new BigDecimal("4984.5528"))
                .totalAnnualPeriod(new BigDecimal("4984.5528"))
                .totalSemiannualPeriod(new BigDecimal("4984.5528"))
                .totalQuarterlyPeriod(new BigDecimal("4984.5528"))
                .totalMonthlyPeriod(new BigDecimal("4984.5528"))
                .build();

        FinancialDataResponseDto financialDataResponseDto = FinancialDataResponseDto.builder()
                .rightPolicy(new BigDecimal("4984.5528"))
                .fractionalSurcharge(new BigDecimal("4984.5528"))
                .vat(new BigDecimal("4984.5528"))
                .build();

        FamilyResponseDto familyResponseDto = new FamilyResponseDto(
                "C",
                "1990-12-21",
                1
        );

        SumNetPremiumResponseDto sumNetPremiumResponseDto = new SumNetPremiumResponseDto(
                "C",
                new BigDecimal("34535.3455")
        );

        InsuredVolunteerResponseDto insuredVolunteerResponseDto = InsuredVolunteerResponseDto.builder()
                .coverages(List.of(coverageResponseDto1, coverageResponseDto2))
                .numberGroup(1)
                .policyHolders(10)
                .families(List.of(familyResponseDto))
                .sumNetPremium(List.of(sumNetPremiumResponseDto))
                .build();

        InsuredVolunteerResponseDto insuredVolunteerResponseDto2 = InsuredVolunteerResponseDto.builder()
                .coverages(List.of(coverageResponseDto1, coverageResponseDto2))
                .numberGroup(2)
                .policyHolders(10)
                .families(List.of(familyResponseDto))
                .sumNetPremium(List.of(sumNetPremiumResponseDto))
                .build();

        PaymentSurchargeResponseDto paymentSurchargeResponseDto = PaymentSurchargeResponseDto.builder()
                .annualSurcharge(new BigDecimal("0.18"))
                .semiannualSurcharge(new BigDecimal("0.18"))
                .quarterlySurcharge(new BigDecimal("0.18"))
                .monthlySurcharge(new BigDecimal("0.18"))
                .build();

        PricingVolunteerResponseDto.VolunteerResponse volunteerResponse = PricingVolunteerResponseDto.VolunteerResponse.builder()
                .totalNetPremium(new BigDecimal("64799.1870"))
                .totalPremium(new BigDecimal("65099.1870"))
                .responsePeriodic(periodicResponseDto)
                .financialDataDocument(financialDataResponseDto)
                .paymentSurcharge(paymentSurchargeResponseDto)
                .insuredVolunteering(List.of(insuredVolunteerResponseDto, insuredVolunteerResponseDto2))
                .build();

        return new PricingVolunteerResponseDto(volunteerResponse);
    }

    private void createFolioVolunteer() {

        FolioNumber folioNumber = new FolioNumber("19991");
        QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        CatalogItem catalogItem = new CatalogItem("001", "GIRO");
        Company company = new Company("acme", catalogItem, 30, 100, 23, new BigDecimal(49), new BigDecimal(25));
        DiffAdminOp diffAdminOp = new DiffAdminOp(30, "lt");
        Insured insured = new Insured(7, 1000, diffAdminOp);
        Age age = new Age(18, 69, 18, 49, null);
        LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null, null, null, null, null, null, null);

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
        folioRecord.setModality("VOLUNTARIA");
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

    private AggregateInsuredGroup createAggregateInsuredGroupVolunteer() {

        final FolioNumber folioNumber = new FolioNumber("19991");

        final QuestionnaireDataEntity questionnaireDataEntity = new QuestionnaireDataEntity(
                170.5,
                59.5,
                true,
                "pfizer",
                true,
                40
        );

        final FamilyMemberEntity familyMemberEntity = new FamilyMemberEntity(
                1,
                "T",
                "Finalizado",
                questionnaireDataEntity,
                "Josep",
                "Pedro",
                "Perez",
                "Gomez",
                LocalDate.now(),
                "M"
        );

        final InsuredEntity insuredEntity = new InsuredEntity(
                "T",
                "user@gmail.com",
                "Finalizado",
                List.of(familyMemberEntity),
                questionnaireDataEntity,
                "Jose",
                "Pedro",
                "Perez",
                "Gomez",
                LocalDate.now(),
                "E",
                234234D,
                "Administrativos"
        );

        final InsuredGroup insuredGroup = new InsuredGroup();
        insuredGroup.setGroupNumber(1);
        insuredGroup.setName("grupo1");
        insuredGroup.setInsuredSumRule("34543.3456");
        insuredGroup.setInsureds(List.of(insuredEntity));

        final InsuredGroupEntity insuredGroupEntity = new InsuredGroupEntity();
        insuredGroupEntity.setId(folioNumber);
        insuredGroupEntity.setGroups(List.of(insuredGroup));
        insuredGroupEntity.setAverageAge(35D);
        insuredGroupEntity.setAdjustedAverageAge(35D);
        insuredGroupEntity.setActuarialAge(35);
        insuredGroupEntity.setDiffActuarialAverageAge(35D);
        insuredGroupEntity.setSami(35D);
        insuredGroupEntity.setStandardDeviation(35D);
        insuredGroupEntity.setQuotient(35D);

        return insuredGroupMapper.toModelGroupInsureds(insuredGroupEntity);
    }

}
