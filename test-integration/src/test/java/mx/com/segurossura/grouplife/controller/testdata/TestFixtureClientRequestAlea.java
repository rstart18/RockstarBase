package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.AttributeRequest;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.AddressClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.CatalogItemEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.ClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.GeneralInfoEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.CostsEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.PolicyEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.QuoteEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestFixtureClientRequestAlea {
    public static FolioRecordEntity createFolioEntityWithoutQuoteToClient() {
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
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null,
                null, null, null);
        final FolioNumber folioNumber = new FolioNumber("9701");
        final CatalogItem catalogItem = new CatalogItem("IT", "IT");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 3, 3, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GeneralInfoEntity generalInfoEntity = new GeneralInfoEntity();
        generalInfoEntity.setTypeLegalId(1);
        generalInfoEntity.setRfc("PEGJ851106LN2");
        generalInfoEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        generalInfoEntity.setEmail("juan.perez@email.com");
        generalInfoEntity.setPhoneNumber("5551234567");
        generalInfoEntity.setName("Juan");
        generalInfoEntity.setSurname("Perez");
        generalInfoEntity.setSecondSurname("Garcia");
        generalInfoEntity.setCurp("PEGJ851106HASRRN08");
        generalInfoEntity.setLegalRepresentativeName("Juan");
        generalInfoEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        generalInfoEntity.setEmail("juan.perez@email.com");
        generalInfoEntity.setPhoneNumber("5551234567");
        final GeneralInfoEntity invoicingEntity = new GeneralInfoEntity();
        invoicingEntity.setTypeLegalId(1);
        invoicingEntity.setRfc("PEGJ851106LN3");
        invoicingEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        invoicingEntity.setEmail("juan.perez@email.com");
        invoicingEntity.setPhoneNumber("5551234567");
        invoicingEntity.setName("Juan");
        invoicingEntity.setSurname("Perez");
        invoicingEntity.setSecondSurname("Garcia");
        invoicingEntity.setCurp("PEGJ851106HASRRN08");
        invoicingEntity.setTaxReform(new CatalogItemEntity("key", "value"));
        invoicingEntity.setReceiverCode("12345");
        final AddressClientEntity addressClientEntity = new AddressClientEntity();
        addressClientEntity.setStreetName("Main St");
        addressClientEntity.setStreetNumberExt("123");
        addressClientEntity.setInternalDepartmentNumber("B");
        addressClientEntity.setZipCode("12345");
        addressClientEntity.setStateId("Jalisco");
        addressClientEntity.setMunicipality("Guadalajara");
        addressClientEntity.setColonyId("Downtown");
        final ClientEntity clientEntity = new ClientEntity();
        clientEntity.setGeneral(invoicingEntity);
        clientEntity.setAddress(addressClientEntity);
        clientEntity.setInvoicing(generalInfoEntity);
        clientEntity.setBillingAddress(addressClientEntity);
        final PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setCurrency("MXN");
        policyEntity.setFormPayment(8);
        policyEntity.setPeriodicity(12);
        policyEntity.setPersonCode(51956230);
        policyEntity.setStatusInsureds(true);
        final CostsEntity costsEntity = new CostsEntity();
        costsEntity.setFolio(9700L);
        costsEntity.setOfficeId(1D);
        costsEntity.setProductId(902D);
        costsEntity.setPolicyState("W");
        costsEntity.setPolicyNumber(3686D);
        costsEntity.setAmount(0D);
        costsEntity.setNetPremium(0D);
        costsEntity.setPolicyRights(0D);
        costsEntity.setPolicySurcharges(0D);
        costsEntity.setNetPremiumTaxes(0D);
        costsEntity.setPolicyRightsTaxes(0D);
        costsEntity.setDiscountValue(0D);
        costsEntity.setFirstPayment(0D);
        costsEntity.setSubsequentPayment(0D);
        policyEntity.setCosts(costsEntity);
        final QuoteEntity quoteEntity = new QuoteEntity();
        quoteEntity.setOfficeId(1);
        quoteEntity.setProductId(902);
        quoteEntity.setStatus("W");
        quoteEntity.setPolicyNumber(3688L);
        quoteEntity.setFolioFromPartner(9700L);
        policyEntity.setQuote(quoteEntity);
        policyEntity.setCosts(costsEntity);
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setModalityValidation(modalityValidation);
        folioRecordEntity.setGroups(List.of(groupVg));
        folioRecordEntity.setClient(clientEntity);
        folioRecordEntity.setPolicy(List.of(policyEntity));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        return folioRecordEntity;
    }

    public static FolioRecordEntity createFolioEntityWithoutPolizy() {
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
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null,
                null, null, null);
        final FolioNumber folioNumber = new FolioNumber("9701");
        final CatalogItem catalogItem = new CatalogItem("IT", "IT");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 3, 3, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GeneralInfoEntity generalInfoEntity = new GeneralInfoEntity();
        generalInfoEntity.setTypeLegalId(2);
        generalInfoEntity.setRfc("EMP241106MG4");
        generalInfoEntity.setBusinessName("empresa");
        generalInfoEntity.setConstitutionDate(LocalDate.parse("2024-11-06"));
        generalInfoEntity.setLegalRepresentativeName("Juan");
        generalInfoEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        generalInfoEntity.setEmail("juan.perez@email.com");
        generalInfoEntity.setPhoneNumber("5551234567");
        final GeneralInfoEntity invoicingEntity = new GeneralInfoEntity();
        invoicingEntity.setTypeLegalId(1);
        invoicingEntity.setRfc("PEGJ851106LN3");
        invoicingEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        invoicingEntity.setEmail("juan.perez@email.com");
        invoicingEntity.setPhoneNumber("5551234567");
        invoicingEntity.setName("Juan");
        invoicingEntity.setSurname("Perez");
        invoicingEntity.setSecondSurname("Garcia");
        invoicingEntity.setCurp("PEGJ851106HASRRN08");
        invoicingEntity.setTaxReform(new CatalogItemEntity("key", "value"));
        invoicingEntity.setReceiverCode("12345");
        final AddressClientEntity addressClientEntity = new AddressClientEntity();
        addressClientEntity.setStreetName("Main St");
        addressClientEntity.setStreetNumberExt("123");
        addressClientEntity.setInternalDepartmentNumber("B");
        addressClientEntity.setZipCode("12345");
        addressClientEntity.setStateId("Jalisco");
        addressClientEntity.setMunicipality("Guadalajara");
        addressClientEntity.setColonyId("Downtown");
        final ClientEntity clientEntity = new ClientEntity();
        clientEntity.setGeneral(generalInfoEntity);
        clientEntity.setAddress(addressClientEntity);
        clientEntity.setInvoicing(invoicingEntity);
        clientEntity.setBillingAddress(addressClientEntity);
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setModalityValidation(modalityValidation);
        folioRecordEntity.setGroups(List.of(groupVg));
        folioRecordEntity.setClient(clientEntity);
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        return folioRecordEntity;
    }

    public static FolioRecordEntity createFolioEntityWithQuoteToClient() {
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
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null,
                null, null, null);
        final FolioNumber folioNumber = new FolioNumber("9700");
        final CatalogItem catalogItem = new CatalogItem("IT", "IT");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final GroupVg groupVg = new GroupVg(1, "estandar", "SAFIJA", 3, 3, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final GeneralInfoEntity generalInfoEntity = new GeneralInfoEntity();
        generalInfoEntity.setTypeLegalId(2);
        generalInfoEntity.setRfc("EMP241106MG4");
        generalInfoEntity.setBusinessName("empresa");
        generalInfoEntity.setConstitutionDate(LocalDate.parse("2024-11-06"));
        generalInfoEntity.setLegalRepresentativeName("Juan");
        generalInfoEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        generalInfoEntity.setEmail("juan.perez@email.com");
        generalInfoEntity.setPhoneNumber("5551234567");
        final GeneralInfoEntity invoicingEntity = new GeneralInfoEntity();
        invoicingEntity.setTypeLegalId(1);
        invoicingEntity.setRfc("PEGJ851106LN3");
        invoicingEntity.setBirthdate(LocalDate.parse("1985-11-06"));
        invoicingEntity.setEmail("juan.perez@email.com");
        invoicingEntity.setPhoneNumber("5551234567");
        invoicingEntity.setName("Juan");
        invoicingEntity.setSurname("Perez");
        invoicingEntity.setSecondSurname("Garcia");
        invoicingEntity.setCurp("PEGJ851106HASRRN08");
        invoicingEntity.setTaxReform(new CatalogItemEntity("key", "value"));
        invoicingEntity.setReceiverCode("12345");
        final AddressClientEntity addressClientEntity = new AddressClientEntity();
        addressClientEntity.setStreetName("Main St");
        addressClientEntity.setStreetNumberExt("123");
        addressClientEntity.setInternalDepartmentNumber("B");
        addressClientEntity.setZipCode("12345");
        addressClientEntity.setStateId("Jalisco");
        addressClientEntity.setMunicipality("Guadalajara");
        addressClientEntity.setColonyId("Downtown");
        final ClientEntity clientEntity = new ClientEntity();
        clientEntity.setGeneral(generalInfoEntity);
        clientEntity.setAddress(addressClientEntity);
        clientEntity.setInvoicing(invoicingEntity);
        clientEntity.setBillingAddress(addressClientEntity);
        final PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setCurrency("MXN");
        policyEntity.setFormPayment(8);
        policyEntity.setPeriodicity(12);
        policyEntity.setPersonCode(51956230);
        policyEntity.setStatusInsureds(true);
        final QuoteEntity quoteEntity = new QuoteEntity();
        quoteEntity.setOfficeId(1);
        quoteEntity.setProductId(902);
        quoteEntity.setStatus("W");
        quoteEntity.setPolicyNumber(3688L);
        quoteEntity.setFolioFromPartner(9700L);
        policyEntity.setQuote(quoteEntity);
        final CostsEntity costsEntity = new CostsEntity();
        costsEntity.setFolio(9700L);
        costsEntity.setOfficeId(1D);
        costsEntity.setProductId(902D);
        costsEntity.setPolicyState("W");
        costsEntity.setPolicyNumber(3686D);
        costsEntity.setAmount(0D);
        costsEntity.setNetPremium(0D);
        costsEntity.setPolicyRights(0D);
        costsEntity.setPolicySurcharges(0D);
        costsEntity.setNetPremiumTaxes(0D);
        costsEntity.setPolicyRightsTaxes(0D);
        costsEntity.setDiscountValue(0D);
        costsEntity.setFirstPayment(0D);
        costsEntity.setSubsequentPayment(0D);
        policyEntity.setCosts(costsEntity);
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
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setModalityValidation(modalityValidation);
        folioRecordEntity.setGroups(List.of(groupVg));
        folioRecordEntity.setClient(clientEntity);
        folioRecordEntity.setPolicy(List.of(policyEntity));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        return folioRecordEntity;
    }

//    private static ClientRequestDto createClientRequestDto(final Integer policyNumber, final Integer officeId,
//                                                           final Integer productId, final String status) {
//        return new ClientRequestDto(policyNumber, officeId, productId, status,null, null,null, null, null);
//    }

    public static List<AttributeRequest> createAttributesPersonCte() {
        final List<AttributeRequest> attributesPersonCte = new ArrayList<>();
        attributesPersonCte.add(new AttributeRequest("CDIDEPER", "Identificador", "EMP241106MG4"));
        attributesPersonCte.add(new AttributeRequest("CDIDEPER", "Identificador", "EMP241106MG4"));
        attributesPersonCte.add(new AttributeRequest("OTFISJUR", "Tipo Persona", "2"));
        attributesPersonCte.add(new AttributeRequest("DSNOMBRE", "Nombre", "empresa"));
        attributesPersonCte.add(new AttributeRequest("DSNOMBR1", "Nombre", null));
        attributesPersonCte.add(new AttributeRequest("DSNOMBR2", "Nombre", null));
        attributesPersonCte.add(new AttributeRequest("DSAPELL1", "Nombre", null));
        attributesPersonCte.add(new AttributeRequest("DSAPELL2", "Nombre", null));
        attributesPersonCte.add(new AttributeRequest("FENACIMI", "Nombre", "2024-11-06"));
        attributesPersonCte.add(new AttributeRequest("OTSEXO", "OTSEXO", null));
        return attributesPersonCte;
    }

    public static List<AttributeRequest> attributesAddressCte() {
        final List<AttributeRequest> attributesAddressCte = new ArrayList<>();
        attributesAddressCte.add(new AttributeRequest("DSDOMICI", "DSDOMICI",
                "Main St"));
        attributesAddressCte.add(new AttributeRequest("CDPOSTAL", "CDPOSTAL",
                "12345"));
        attributesAddressCte.add(new AttributeRequest("OTPOBLAC", "ALCALDIA",
                "Guadalajara"));
        attributesAddressCte.add(new AttributeRequest("CDCOLONI", "COLONIA",
                "empresa"));
        attributesAddressCte.add(new AttributeRequest("CDPROVIN", "ESTADO",
                "Jalisco"));
        attributesAddressCte.add(new AttributeRequest("OTPISO", "NUMERO EXTERIOR",
                "123"));
        attributesAddressCte.add(new AttributeRequest("NMNUMERO", "NUMERO INTERIOR",
                null));
        return attributesAddressCte;
    }
}
