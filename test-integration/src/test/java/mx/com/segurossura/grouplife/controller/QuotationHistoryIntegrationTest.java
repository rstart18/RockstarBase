package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.ClientEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.GeneralInfoEntity;
import mx.com.segurossura.grouplife.openapi.model.*;
import mx.com.segurossura.grouplife.utils.FolioRecordEntityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuotationHistoryIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/quotation-history/get";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";


    @Test
    void test_quotationHistory() {

        final String numberFolio = "201";
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordEntity(numberFolio);
        LocalDateTime creationDate = LocalDateTime.now();
        folioRecordEntity.setModality("TRADICIONAL");
        folioRecordEntity.setCreatedAt(creationDate);
        ClientEntity clientEntity = new ClientEntity();
        GeneralInfoEntity generalInfoEntity = new GeneralInfoEntity();
        generalInfoEntity.setBusinessName("EMPRESA");
        clientEntity.setGeneral(generalInfoEntity);
        folioRecordEntity.setClient(clientEntity);
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final String numberFolio2 = "202";
        final FolioRecordEntity folioRecordEntity2 = FolioRecordEntityUtil.createBaseFolioRecordEntity(numberFolio2);
        LocalDateTime creationDate2 = LocalDateTime.now();
        folioRecordEntity2.setModality("TRADICIONAL");
        folioRecordEntity2.setCreatedAt(creationDate2);
        ClientEntity clientEntity2 = new ClientEntity();
        GeneralInfoEntity generalInfoEntity2 = new GeneralInfoEntity();
        generalInfoEntity2.setName("NAME");
        generalInfoEntity2.setSurname("LASTNAME");
        clientEntity2.setGeneral(generalInfoEntity2);
        folioRecordEntity2.setClient(clientEntity2);
        this.reactiveMongoTemplate.insert(folioRecordEntity2).block();

        int pageSize = 100;
        int page = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        final HistoryRequestDto historyRequestDto = new HistoryRequestDto();
        historyRequestDto.setEmail("");
        historyRequestDto.setStartDate(LocalDate.now().minusDays(1));
        historyRequestDto.setEndDate(LocalDate.now().plusDays(1));
        historyRequestDto.setFolioNumber(null);
        historyRequestDto.setNameOrBusinessName(null);

        HistoryResponseDto historyResponseDto = new HistoryResponseDto();
        historyResponseDto.email("agent@mail.com");
        historyResponseDto.setUserId("OPS$");
        historyResponseDto.creationDate(
                folioRecordEntity.getCreatedAt().atZone(ZoneId.systemDefault()).format(formatter)
        );
        historyResponseDto.branchId("902");
        historyResponseDto.officeId("1");
        historyResponseDto.folio("201");
        historyResponseDto.name("Jane Doe");
        historyResponseDto.setNameOrBusinessName("EMPRESA");

        HistoryResponseDto historyResponseDto2 = new HistoryResponseDto();
        historyResponseDto2.email("agent@mail.com");
        historyResponseDto2.setUserId("OPS$");
        historyResponseDto2.creationDate(
                folioRecordEntity2.getCreatedAt().atZone(ZoneId.systemDefault()).format(formatter)
        );
        historyResponseDto2.branchId("902");
        historyResponseDto2.officeId("1");
        historyResponseDto2.folio("202");
        historyResponseDto2.name("Jane Doe");
        historyResponseDto2.setNameOrBusinessName("NAME LASTNAME");

        final GetHistoryFolios206ResponseDto expect = new GetHistoryFolios206ResponseDto();
        expect.setPaging(new StandardPagingDto(page, pageSize, null));
        expect.setData(
                List.of(historyResponseDto, historyResponseDto2)
        );

        final String uri = UriComponentsBuilder.fromPath(BASE_PATH)
                .queryParam("$limit", pageSize)
                .queryParam("$init", page)
                .queryParam("modality", "TRADICIONAL")
                .toUriString();

        final GetHistoryFolios206ResponseDto response = this.webTestClient.post()
                .uri(uri)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(historyRequestDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(GetHistoryFolios206ResponseDto.class)
                .returnResult().getResponseBody();

        assertEquals(expect, response);
    }

}
