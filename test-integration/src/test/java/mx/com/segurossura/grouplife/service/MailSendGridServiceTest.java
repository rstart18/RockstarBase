package mx.com.segurossura.grouplife.service;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.application.port.MailSendGridPort;
import mx.com.segurossura.grouplife.application.service.MailSendGridService;
import mx.com.segurossura.grouplife.application.service.ServiceUtils;
import mx.com.segurossura.grouplife.infrastructure.mapper.FolioRecordMapperImpl;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.CostsEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.PolicyEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.quote.QuoteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailSendGridServiceTest extends BaseIT {

    private MailSendGridService mailSendGridService;
    private MailSendGridPort mailSendGridPort;
    private DbPort dbPort;
    private CatalogPort catalogPort;
    private ServiceUtils serviceUtils;

    @BeforeEach
    void setUp() {
        this.mailSendGridPort = mock(MailSendGridPort.class);
        this.dbPort = mock(DbPort.class);
        this.catalogPort = mock(CatalogPort.class);
        this.serviceUtils = new ServiceUtils(this.dbPort, this.catalogPort);
        this.mailSendGridService = new MailSendGridService(
                this.mailSendGridPort,
                this.serviceUtils
        );
    }

    @Test
    void test_sendMail() {

        //Given
        final String numberFolio = "7798";
        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe2")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setStatus("Abierto");
        folioRecordEntity.setModality("TRADITIONAL");

        final PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setCurrency("MXN");
        policyEntity.setFormPayment(3);
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
        policyEntity.setIssue(quoteEntity);
        policyEntity.setCosts(costsEntity);

        folioRecordEntity.setPolicy(List.of(policyEntity));

        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        FolioRecordMapperImpl folioRecordMapper = new FolioRecordMapperImpl();
        FolioRecord folioRecord = folioRecordMapper.toDomain(folioRecordEntity);

        when(this.mailSendGridPort.sendNotificationMail(any(), eq("7798"), eq(true), eq(3), eq(true), eq(folioRecord.modality()))).thenReturn(Mono.just("OK"));
        when(this.serviceUtils.getFolioRecord("7798")).thenReturn(Mono.just(folioRecord));

        StepVerifier.create(this.mailSendGridService.sendMail("7798"))
                .expectNext("OK")
                .verifyComplete();

        Mockito.verify(this.mailSendGridPort).sendNotificationMail(any(), eq("7798"), eq(true), eq(3), eq(true), eq(folioRecord.modality()));
    }
}
