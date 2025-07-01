package mx.com.segurossura.grouplife.utils;

import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.domain.model.enums.StatusFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FolioRecordEntityUtil {

    public static FolioRecordEntity createBaseFolioRecordEntity(String numberFolio) {

        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("OPS$")
                .pointOfSaleId("100001")
                .groupId("05470")
                .subgroupId("0547000001")
                .rateProfileId("9020000001")
                .name("Jane Doe")
                .email("agent@mail.com")
                .officeId("1")
                .officeDescription("Main Office")
                .agentId("000001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("967")
                .build();
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(recordFolio);
        folioRecordEntity.setUpdatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecordEntity.setStatus("Abierto");

        return folioRecordEntity;
    }

    public static FolioRecordEntity createBaseFolioRecordWithAgentEntity(String numberFolio, RecordFolio agentData) {

        final Plan plan = new Plan("", "", List.of());

        final Company company = new Company("TEST COMPANY",
                CatalogItem.builder().key("01").value("GIRO").build(),
                40,
                100,
                20,
                new BigDecimal(150000),
                new BigDecimal(120000));

        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(agentData);
        folioRecordEntity.setUpdatedAt(LocalDateTime.now());
        folioRecordEntity.setCreatedAt(LocalDateTime.now());
        folioRecordEntity.setStatus(StatusFolio.ABIERTO.getValue());
        folioRecordEntity.setModality(Modality.TRADITIONAL.getMessageFormat());
        folioRecordEntity.setQuotationDetails(new QuotationDetails(0.2D, 0.01D,
                0.2D, 0.01D, LocalDate.now(), LocalDate.now().plusYears(1),
                "C", "DETALLADA"));
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setPlan(plan);
        folioRecordEntity.setModality("TRADICIONAL");

        return folioRecordEntity;
    }

    public static FolioRecordEntity createBaseFolioRecordExpired(String numberFolio, RecordFolio agentData) {


        final Plan plan = new Plan("", "", List.of());

        final Company company = new Company("TEST COMPANY",
                CatalogItem.builder().key("01").value("GIRO").build(),
                40,
                100,
                20,
                new BigDecimal(150000),
                new BigDecimal(120000));

        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final FolioRecordEntity folioRecordEntity = new FolioRecordEntity();
        folioRecordEntity.setId(folioNumber);
        folioRecordEntity.setAgentData(agentData);
        folioRecordEntity.setUpdatedAt(LocalDateTime.now().minusDays(20));
        folioRecordEntity.setCreatedAt(LocalDateTime.now().minusDays(20));
        folioRecordEntity.setStatus(StatusFolio.EXPIRADO.getValue());
        folioRecordEntity.setModality(Modality.TRADITIONAL.getMessageFormat());
        folioRecordEntity.setQuotationDetails(new QuotationDetails(0.2D, 0.01D,
                0.2D, 0.01D, LocalDate.now(), LocalDate.now().plusYears(1),
                "C", "DETALLADA"));
        folioRecordEntity.setCompany(company);
        folioRecordEntity.setPlan(plan);
        folioRecordEntity.setModality("TRADICIONAL");


        return folioRecordEntity;
    }
}
