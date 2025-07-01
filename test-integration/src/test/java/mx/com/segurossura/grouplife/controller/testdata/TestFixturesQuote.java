package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.InsuredRequestDto;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.AddressClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.CatalogItemEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.ClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.GeneralInfoEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredGroup;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredGroupEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.CostsEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.PolicyEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.QuoteEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestFixturesQuote {

    public static FolioRecordEntity createFolioEntity() {
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

        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null, null, null,
                null, null, null);
        final FolioNumber folioNumber = new FolioNumber("97");
        final CatalogItem catalogItem = new CatalogItem("IT", "IT");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final GroupVg groupVg = new GroupVg(1, "GRUPO01", "SA_FIJA", 70, 30, new BigDecimal(12000), new BigDecimal(12000), new BigDecimal("12.12"),
                null, null, null, List.of(coverageDetail));
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("OPS$LALOZANO")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("email")
                .officeId("01")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();

        QuoteEntity quote = new QuoteEntity();
        quote.setOfficeId(1);
        quote.setProductId(902);
        quote.setStatus("W");
        quote.setPolicyNumber(789999L);
        quote.setFolioFromPartner(97L);

        CostsEntity costs = new CostsEntity();
        costs.setFolio(97L);
        costs.setOfficeId(1D);
        costs.setProductId(902D);
        costs.setPolicyState("W");
        costs.setPolicyNumber(789999D);
        costs.setAmount(465456546D);
        costs.setNetPremium(78897456D);
        costs.setPolicyRights(300D);
        costs.setPolicySurcharges(100D);
        costs.setNetPremiumTaxes(45546D);
        costs.setPolicyRightsTaxes(300D);
        costs.setFirstPayment(7878897D);
        costs.setSubsequentPayment(231231D);

        final PolicyEntity policy = new PolicyEntity();
        policy.setQuote(quote);
        policy.setCosts(costs);
        policy.setCurrency("MXN");
        policy.setFormPayment(1);
        policy.setPeriodicity(12);
        policy.setPersonCode(125666);
        policy.setStatusClient(false);
        policy.setStatusInsureds(false);
        policy.setCreatedAt(LocalDateTime.now());
        policy.setStatusIssue("FAILED");
        //policy.setRequestId("9");

        GeneralInfoEntity general = new GeneralInfoEntity();
        general.setTypeLegalId(2);
        general.setRfc("EGJ851106LN3");
        general.setBusinessName("Empresa XYZ S.A. de C.V.");
        general.setConstitutionDate(LocalDate.now().withYear(2000));
        general.setLegalRepresentativeName("Juan Pérez García");
        general.setBirthdate(LocalDate.now().withYear(2000));
        general.setEmail("juan.perez@email.com");
        general.setPhoneNumber("5551234567");
        general.setName("");
        general.setSecondName("");
        general.setSurname("");
        general.setSecondSurname("");
        general.setGender("");
        general.setTaxReform(new CatalogItemEntity("key1", "value 1"));
        general.setReceiverCode("00001");

        AddressClientEntity address = new AddressClientEntity();
        address.setStreetName("Calle Ficticia 123");
        address.setStreetNumberExt("123AB");
        address.setInternalDepartmentNumber("123AB");
        address.setZipCode("00001");
        address.setStateId("Nuevo León");
        address.setMunicipality("Guadalajara");
        address.setColonyId("Centro");
        address.setOrdinalDomicile(1);

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setGeneral(general);
        clientEntity.setAddress(address);
        clientEntity.setInvoicing(general);
        clientEntity.setBillingAddress(address);

        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setModalityValidation(modalityValidation);
        folioRecordEntity.setGroups(List.of(groupVg));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecordEntity.setPolicy(List.of(policy));
        folioRecordEntity.setClient(clientEntity);
        return folioRecordEntity;
    }

    public static FolioRecordEntity createFolioEntity2() {
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

        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null, null, null,
                null, null, null);
        final FolioNumber folioNumber = new FolioNumber("98");
        final CatalogItem catalogItem = new CatalogItem("IT", "IT");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final GroupVg groupVg = new GroupVg(1, "GRUPO01", "SA_FIJA", 70, 30, new BigDecimal(12000), new BigDecimal(12000), new BigDecimal("12.12"),
                null, null, null, List.of(coverageDetail));
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("OPS$LALOZANO")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("email")
                .officeId("01")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();

        GeneralInfoEntity general = new GeneralInfoEntity();
        general.setTypeLegalId(2);
        general.setRfc("EGJ851106LN3");
        general.setBusinessName("Empresa XYZ S.A. de C.V.");
        general.setConstitutionDate(LocalDate.now().withYear(2000));
        general.setLegalRepresentativeName("Juan Pérez García");
        general.setBirthdate(LocalDate.now().withYear(2000));
        general.setEmail("juan.perez@email.com");
        general.setPhoneNumber("5551234567");
        general.setName("");
        general.setSecondName("");
        general.setSurname("");
        general.setSecondSurname("");
        general.setGender("");
        general.setTaxReform(new CatalogItemEntity("key1", "value 1"));
        general.setReceiverCode("00001");

        AddressClientEntity address = new AddressClientEntity();
        address.setStreetName("Calle Ficticia 123");
        address.setStreetNumberExt("123AB");
        address.setInternalDepartmentNumber("123AB");
        address.setZipCode("00001");
        address.setStateId("Nuevo León");
        address.setMunicipality("Guadalajara");
        address.setColonyId("Centro");
        address.setOrdinalDomicile(1);

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setGeneral(general);
        clientEntity.setAddress(address);
        clientEntity.setInvoicing(general);
        clientEntity.setBillingAddress(address);

        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setModalityValidation(modalityValidation);
        folioRecordEntity.setGroups(List.of(groupVg));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecordEntity.setClient(clientEntity);
        return folioRecordEntity;
    }

    public static FolioRecordEntity createFolioEntity3() {
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

        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null, null, null,
                null, null, null);
        final FolioNumber folioNumber = new FolioNumber("99");
        final CatalogItem catalogItem = new CatalogItem("IT", "IT");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final GroupVg groupVg = new GroupVg(1, "GRUPO01", "SA_FIJA", 70, 30, new BigDecimal(12000), new BigDecimal(12000), new BigDecimal("12.12"),
                null, null, null, List.of(coverageDetail));
        final QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("OPS$LALOZANO")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("email")
                .officeId("01")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();

        QuoteEntity quote = new QuoteEntity();
        quote.setOfficeId(1);
        quote.setProductId(902);
        quote.setStatus("W");
        quote.setPolicyNumber(789999L);
        quote.setFolioFromPartner(97L);

        CostsEntity costs = new CostsEntity();
        costs.setFolio(97L);
        costs.setOfficeId(1D);
        costs.setProductId(902D);
        costs.setPolicyState("W");
        costs.setPolicyNumber(789999D);
        costs.setAmount(465456546D);
        costs.setNetPremium(78897456D);
        costs.setPolicyRights(300D);
        costs.setPolicySurcharges(100D);
        costs.setNetPremiumTaxes(45546D);
        costs.setPolicyRightsTaxes(300D);
        costs.setFirstPayment(7878897D);
        costs.setSubsequentPayment(231231D);

        final PolicyEntity policy = new PolicyEntity();
        policy.setQuote(quote);
        policy.setCosts(costs);
        policy.setCurrency("MXN");
        policy.setFormPayment(1);
        policy.setPeriodicity(12);
        policy.setPersonCode(125666);
        policy.setStatusClient(false);
        policy.setStatusInsureds(true);
        policy.setCreatedAt(LocalDateTime.now());
        policy.setStatusIssue("FAILED");
        //policy.setRequestId("9");

        GeneralInfoEntity general = new GeneralInfoEntity();
        general.setTypeLegalId(1);
        general.setRfc("EGJ851106LN3");
        general.setBusinessName("Empresa XYZ S.A. de C.V.");
        general.setConstitutionDate(LocalDate.now().withYear(2000));
        general.setLegalRepresentativeName("Juan Pérez García");
        general.setBirthdate(LocalDate.now().withYear(2000));
        general.setEmail("juan.perez@email.com");
        general.setPhoneNumber("5551234567");
        general.setName("");
        general.setSecondName("");
        general.setSurname("");
        general.setSecondSurname("");
        general.setGender("");
        general.setTaxReform(new CatalogItemEntity("key1", "value 1"));
        general.setReceiverCode("00001");

        AddressClientEntity address = new AddressClientEntity();
        address.setStreetName("Calle Ficticia 123");
        address.setStreetNumberExt("123AB");
        address.setInternalDepartmentNumber("123AB");
        address.setZipCode("00001");
        address.setStateId("Nuevo León");
        address.setMunicipality("Guadalajara");
        address.setColonyId("Centro");
        address.setOrdinalDomicile(1);

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setGeneral(general);
        clientEntity.setAddress(address);
        clientEntity.setInvoicing(general);
        clientEntity.setBillingAddress(address);

        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setModalityValidation(modalityValidation);
        folioRecordEntity.setGroups(List.of(groupVg));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecordEntity.setPolicy(List.of(policy));
        folioRecordEntity.setClient(clientEntity);
        return folioRecordEntity;
    }

    public static InsuredGroupEntity createGroupEntity() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        final FolioNumber folioNumber = new FolioNumber("97");
        final LocalDateTime createdAt = LocalDateTime.parse("2024-07-20T15:31:11.141");
        final LocalDateTime updatedAt = LocalDateTime.parse("2024-07-20T15:31:11.141");
        final InsuredGroup insuredGroup = new InsuredGroup();
        insuredGroup.setGroupNumber(1);
        insuredGroup.setName("GRUPO01");
        insuredGroup.setInsuredSumRule("SA_FIJA");
        insuredGroup.setSalaryMonth(0);

        final LocalDateTime insuredDate = LocalDateTime.parse("2024-07-20T15:31:11.141", formatter);

        final InsuredEntity insured1 = createInsuredEntity(1, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured2 = createInsuredEntity(2, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured3 = createInsuredEntity(3, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured4 = createInsuredEntity(4, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");
        final InsuredEntity insured5 = createInsuredEntity(5, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");
        final InsuredEntity insured6 = createInsuredEntity(6, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");

        final List<InsuredEntity> insureds = new ArrayList<>();
        insureds.add(insured1);
        insureds.add(insured2);
        insureds.add(insured3);
        insureds.add(insured4);
        insureds.add(insured5);
        insureds.add(insured6);

        insuredGroup.setInsureds(insureds);

        final List<InsuredGroup> groups = new ArrayList<>();
        groups.add(insuredGroup);

        final InsuredGroupEntity insuredGroupEntity = new InsuredGroupEntity();
        insuredGroupEntity.setId(folioNumber);
        insuredGroupEntity.setCreatedAt(createdAt);
        insuredGroupEntity.setUpdatedAt(updatedAt);
        insuredGroupEntity.setGroups(groups);
        insuredGroupEntity.setAverageAge(24.0);
        insuredGroupEntity.setAdjustedAverageAge(19.0);
        insuredGroupEntity.setActuarialAge(24);
        insuredGroupEntity.setDiffActuarialAverageAge(0.0);
        insuredGroupEntity.setSami(761538.46);
        insuredGroupEntity.setStandardDeviation(526125.17);
        insuredGroupEntity.setQuotient(12.5);

        return insuredGroupEntity;
    }

    public static InsuredGroupEntity createGroupEntity2() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        final FolioNumber folioNumber = new FolioNumber("98");
        final LocalDateTime createdAt = LocalDateTime.parse("2024-07-20T15:31:11.141");
        final LocalDateTime updatedAt = LocalDateTime.parse("2024-07-20T15:31:11.141");
        final InsuredGroup insuredGroup = new InsuredGroup();
        insuredGroup.setGroupNumber(1);
        insuredGroup.setName("GRUPO01");
        insuredGroup.setInsuredSumRule("SA_FIJA");
        insuredGroup.setSalaryMonth(0);

        final LocalDateTime insuredDate = LocalDateTime.parse("2024-07-20T15:31:11.141", formatter);

        final InsuredEntity insured1 = createInsuredEntity(1, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured2 = createInsuredEntity(2, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured3 = createInsuredEntity(3, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured4 = createInsuredEntity(4, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");
        final InsuredEntity insured5 = createInsuredEntity(5, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");
        final InsuredEntity insured6 = createInsuredEntity(6, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");

        final List<InsuredEntity> insureds = new ArrayList<>();
        insureds.add(insured1);
        insureds.add(insured2);
        insureds.add(insured3);
        insureds.add(insured4);
        insureds.add(insured5);
        insureds.add(insured6);

        insuredGroup.setInsureds(insureds);

        final List<InsuredGroup> groups = new ArrayList<>();
        groups.add(insuredGroup);

        final InsuredGroupEntity insuredGroupEntity = new InsuredGroupEntity();
        insuredGroupEntity.setId(folioNumber);
        insuredGroupEntity.setCreatedAt(createdAt);
        insuredGroupEntity.setUpdatedAt(updatedAt);
        insuredGroupEntity.setGroups(groups);
        insuredGroupEntity.setAverageAge(24.0);
        insuredGroupEntity.setAdjustedAverageAge(19.0);
        insuredGroupEntity.setActuarialAge(24);
        insuredGroupEntity.setDiffActuarialAverageAge(0.0);
        insuredGroupEntity.setSami(761538.46);
        insuredGroupEntity.setStandardDeviation(526125.17);
        insuredGroupEntity.setQuotient(12.5);

        return insuredGroupEntity;
    }


    public static InsuredGroupEntity createGroupEntity3() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        final FolioNumber folioNumber = new FolioNumber("99");
        final LocalDateTime createdAt = LocalDateTime.parse("2024-07-20T15:31:11.141");
        final LocalDateTime updatedAt = LocalDateTime.parse("2024-07-20T15:31:11.141");
        final InsuredGroup insuredGroup = new InsuredGroup();
        insuredGroup.setGroupNumber(1);
        insuredGroup.setName("GRUPO01");
        insuredGroup.setInsuredSumRule("SA_FIJA");
        insuredGroup.setSalaryMonth(0);

        final LocalDateTime insuredDate = LocalDateTime.parse("2024-07-20T15:31:11.141", formatter);

        final InsuredEntity insured1 = createInsuredEntity(1, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured2 = createInsuredEntity(2, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured3 = createInsuredEntity(3, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "OPERATIVOS");
        final InsuredEntity insured4 = createInsuredEntity(4, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");
        final InsuredEntity insured5 = createInsuredEntity(5, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");
        final InsuredEntity insured6 = createInsuredEntity(6, "alex", "lopez", "arbith", "lora",
                LocalDate.from(insuredDate), "H", 10000.0, "ADMINISTRATIVOS");

        final List<InsuredEntity> insureds = new ArrayList<>();
        insureds.add(insured1);
        insureds.add(insured2);
        insureds.add(insured3);
        insureds.add(insured4);
        insureds.add(insured5);
        insureds.add(insured6);

        insuredGroup.setInsureds(insureds);

        final List<InsuredGroup> groups = new ArrayList<>();
        groups.add(insuredGroup);

        final InsuredGroupEntity insuredGroupEntity = new InsuredGroupEntity();
        insuredGroupEntity.setId(folioNumber);
        insuredGroupEntity.setCreatedAt(createdAt);
        insuredGroupEntity.setUpdatedAt(updatedAt);
        insuredGroupEntity.setGroups(groups);
        insuredGroupEntity.setAverageAge(24.0);
        insuredGroupEntity.setAdjustedAverageAge(19.0);
        insuredGroupEntity.setActuarialAge(24);
        insuredGroupEntity.setDiffActuarialAverageAge(0.0);
        insuredGroupEntity.setSami(761538.46);
        insuredGroupEntity.setStandardDeviation(526125.17);
        insuredGroupEntity.setQuotient(12.5);

        return insuredGroupEntity;
    }

    public static InsuredEntity createInsuredEntity(final Integer id, final String name, final String secondName,
                                                    final String surname, final String secondSurname, final LocalDate birthDate,
                                                    final String gender, final Double monthlySalary,
                                                    final String occupation) {
        return new InsuredEntity(null, null, null, null, null, name, secondName, surname, secondSurname, birthDate,
                gender, monthlySalary, occupation);
    }

    public static InsuredRequestDto createInsured(final String name, final String secondName, final String surname,
                                                  final String secondSurname, final LocalDate birthDate,
                                                  final Integer age, final String gender, final String questionnaire,
                                                  final String kindshipId, final String rfc, final String email,
                                                  final String phoneNumber, final Integer situationNumber,
                                                  final String salaryMonth, final String insuredSumSalary) {
        return new InsuredRequestDto(name, secondName, surname, secondSurname, birthDate,
                age, gender, questionnaire, kindshipId, rfc, email, phoneNumber, situationNumber, salaryMonth,
                insuredSumSalary);
    }
}
