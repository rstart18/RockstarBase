package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.AddressClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.CatalogItemEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.ClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.Costs;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.GeneralInfoEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.CostsEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.PolicyEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.QuoteEntity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class GetFolioIntegrationTest extends BaseIT {
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final Logger log = LoggerFactory.getLogger(GetFolioIntegrationTest.class);

    @Test
    void testGetFolio() {
        this.createFolio();
        final int folioNumber = 8;
        this.webTestClient.get()
                .uri("/folio/" + folioNumber)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response -> {
                    System.out.println("Response body: " + new String(response.getResponseBody()));
                })
                .jsonPath("$.data.numberFolio").isEqualTo("8")
                .jsonPath("$.data.createdAt").isEqualTo("2023-11-09T11:50:58.389Z")
                //.jsonPath("$.data.updatedAt").isEqualTo(LocalDateTime.now())
                .jsonPath("$.data.plan").isEqualTo(null)
                .jsonPath("$.data.agentData.userId").isEqualTo("OPS$LALOZANO")
                .jsonPath("$.data.agentData.pointOfSaleId").isEqualTo("100001")
                .jsonPath("$.data.agentData.groupId").isEqualTo("05470")
                .jsonPath("$.data.agentData.subgroupId").isEqualTo("0547000001")
                .jsonPath("$.data.agentData.rateProfileId").isEqualTo("9020000001")
                .jsonPath("$.data.agentData.name").isEqualTo("Luis Antonio")
                .jsonPath("$.data.agentData.email").isEqualTo("Luis.Lozano@segurossura.com.mx")
                .jsonPath("$.data.agentData.officeId").isEqualTo("1")
                .jsonPath("$.data.agentData.officeDescription").isEqualTo("OFICINA MEXICO, D.F.")
                .jsonPath("$.data.agentData.agentId").isEqualTo("000001")
                .jsonPath("$.data.agentData.agentName").isEqualTo("AGENTE DIRECTO")
                .jsonPath("$.data.agentData.promoterName").isEqualTo("Alejandro Perea Mejia (METROPOLITANA)")
                .jsonPath("$.data.agentData.promoterId").isEqualTo("967")
                .jsonPath("$.data.quotationDetails.agentInitCommissionPercentage").isEqualTo(0.2)
                .jsonPath("$.data.quotationDetails.promoterInitCommissionPercentage").isEqualTo(0.01)
                .jsonPath("$.data.quotationDetails.agentCommissionPercentage").isEqualTo(0.2)
                .jsonPath("$.data.quotationDetails.promoterCommissionPercentage").isEqualTo(0.01)
                .jsonPath("$.data.quotationDetails.effectiveDate").isEqualTo("2024-11-09")
                .jsonPath("$.data.quotationDetails.businessDivision").isEqualTo("C")
                .jsonPath("$.data.quotationDetails.administrationType").isEqualTo("DETALLADA")
                .jsonPath("$.data.modalityValidation.insured.min").isEqualTo(7)
                .jsonPath("$.data.modalityValidation.insured.max").isEqualTo(1000)
                .jsonPath("$.data.modalityValidation.insured.diffAdminOp.percentage").isEqualTo(30)
                .jsonPath("$.data.modalityValidation.insured.diffAdminOp.comparator").isEqualTo("lt")
                .jsonPath("$.data.modalityValidation.age.min").isEqualTo(18)
                .jsonPath("$.data.modalityValidation.age.max").isEqualTo(69)
                .jsonPath("$.data.modalityValidation.age.averageMin").isEqualTo(18)
                .jsonPath("$.data.modalityValidation.age.averageMax").isEqualTo(49)
                .jsonPath("$.data.modality").isEqualTo("TRADICIONAL")
                .jsonPath("$.data.company.name").isEqualTo("f")
                .jsonPath("$.data.company.businessActivity.key").isEqualTo("001")
                .jsonPath("$.data.company.businessActivity.value").isEqualTo("GIRO")
                .jsonPath("$.data.company.averageAgeInsured").isEqualTo(30)
                .jsonPath("$.data.company.numAdministrativeInsured").isEqualTo(100)
                .jsonPath("$.data.company.numOperationalInsured").isEqualTo(23)
                .jsonPath("$.data.company.administrativeInsuredSum").isEqualTo(49)
                .jsonPath("$.data.company.operationalInsuredSum").isEqualTo(25)
                .jsonPath("$.data.costs.totalPremium").isEqualTo(BigDecimal.valueOf(15000.0))
                .jsonPath("$.data.costs.totalNetPremium").isEqualTo(BigDecimal.valueOf(15000.0))
                .jsonPath("$.data.client.general.rfc").isEqualTo("EMP241106MG4")
                .jsonPath("$.data.client.invoicing.rfc").isEqualTo("PEGJ851106LN3")
                .jsonPath("$.data.client.invoicing.taxReform.key").isEqualTo("key")
                .jsonPath("$.data.client.invoicing.taxReform.value").isEqualTo("value")
                .jsonPath("$.data.client.invoicing.receiverCode").isEqualTo("12345")
                .jsonPath("$.data.client.address.zipCode").isEqualTo("12345");
    }


    private void createFolio() {

        FolioNumber folioNumber = new FolioNumber("8");
        QuotationDetails quotationDetails = new QuotationDetails(0.2, 0.01, 0.2,
                0.01, LocalDate.parse("2024-11-09"), LocalDate.parse("2025-11-09"), "C", "DETALLADA");
        CatalogItem catalogItem = new CatalogItem("001", "GIRO");
        Company company = new Company("f", catalogItem, 30, 100, 23, new BigDecimal(49), new BigDecimal(25));
        DiffAdminOp diffAdminOp = new DiffAdminOp(30, "lt");
        Insured insured = new Insured(7, 1000, diffAdminOp);
        Age age = new Age(18, 69, 18, 49, null);
        LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 10, null,null, null, null, null, null, null);
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
        final Costs costs = new Costs();
        costs.setTotalNetPremium(BigDecimal.valueOf(15000.0));
        costs.setTotalPremium(BigDecimal.valueOf(15000.0));
        costs.setRights(BigDecimal.valueOf(15000.0));
        costs.setSurcharges(BigDecimal.valueOf(15000.0));
        costs.setVat(BigDecimal.valueOf(20000));
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

        final PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setCurrency("MXN");
        policyEntity.setFormPayment(8);
        policyEntity.setPaymentLink(true);
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

        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setGroups(List.of());
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setPlan(null);
        folioRecord.setCompany(company);
        folioRecord.setCosts(costs);
        folioRecord.setClient(clientEntity);
        folioRecord.setCreatedAt(LocalDateTime.parse("2023-11-09T11:50:58.389"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-11-09T15:07:46.76"));
        folioRecord.setQuotationDetails(quotationDetails);
        folioRecord.setStatus("Abierto");
        folioRecord.setPolicy(List.of(policyEntity));

        this.reactiveMongoTemplate.insert(folioRecord).block();
    }

}
