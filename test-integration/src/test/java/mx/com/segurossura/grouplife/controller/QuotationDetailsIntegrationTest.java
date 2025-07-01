package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.domain.model.enums.AdministrationType;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.QuotationDetails;
import mx.com.segurossura.grouplife.openapi.model.AdministrationTypeDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardResponseMessageDto;
import mx.com.segurossura.grouplife.openapi.model.UpdateQuotationDetails200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.UpdateQuotationDetailsRequestDto;
import mx.com.segurossura.grouplife.utils.FolioRecordEntityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuotationDetailsIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/quotation-details/{numberFolio}";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    private static Stream<Arguments> updateQuotationDetailsValidations() {

        final QuotationDetails quotationDetails1 = new QuotationDetails(0.2D, 0.01D, 0.2D, 0.01D, LocalDate.now(), LocalDate.now().plusYears(1), "C", "DETALLADA");
        final UpdateQuotationDetailsRequestDto updateQuotationDetailsRequestDto1 = new UpdateQuotationDetailsRequestDto();
        updateQuotationDetailsRequestDto1.agentCommission(null);
        updateQuotationDetailsRequestDto1.effectiveDate(LocalDate.now().minusDays(1L));
        final StandardErrorDto errorItem1 = new StandardErrorDto(
                "VG-QD-01",
                "La fecha inicio de vigencia no puede ser menor a la fecha de hoy o superar 30 días de posterioridad");
        errorItem1.setField("effectiveDate");

        final QuotationDetails quotationDetails2 = new QuotationDetails(0.2D, 0.01D, 0.2D, 0.01D, LocalDate.now(), LocalDate.now().plusYears(1), "C", "DETALLADA");
        final UpdateQuotationDetailsRequestDto updateQuotationDetailsRequestDto2 = new UpdateQuotationDetailsRequestDto();
        updateQuotationDetailsRequestDto2.agentCommission(0.5d);
        updateQuotationDetailsRequestDto2.effectiveDate(null);
        final StandardErrorDto errorItem2 = new StandardErrorDto(
                "VG-QD-02",
                "La comisión del agente no puede superar al máximo permitido de 20%");
        errorItem2.setField("agentCommission");

        final QuotationDetails quotationDetails3 = new QuotationDetails(0.2D, 0.01D, 0.2D, 0.01D, LocalDate.now(), LocalDate.now().plusYears(1), "C", "DETALLADA");
        final UpdateQuotationDetailsRequestDto updateQuotationDetailsRequestDto3 = new UpdateQuotationDetailsRequestDto();
        updateQuotationDetailsRequestDto3.agentCommission(null);
        updateQuotationDetailsRequestDto3.effectiveDate(null);
        updateQuotationDetailsRequestDto3.administrationType(AdministrationTypeDto.AUTOADMINISTRADA);
        final StandardErrorDto errorItem3 = new StandardErrorDto(
                "VG-QD-03",
                "El tipo de administración no se puede modificar");
        errorItem3.setField("administrationType");

        return Stream.of(
                Arguments.of("Request with invalid effectiveDate and commission null", quotationDetails1, updateQuotationDetailsRequestDto1, errorItem1),
                Arguments.of("Request with invalid commission and effectiveDate null", quotationDetails2, updateQuotationDetailsRequestDto2, errorItem2),
                Arguments.of("Request with invalid administrationType", quotationDetails3, updateQuotationDetailsRequestDto3, errorItem3)
        );
    }

    @Test
    void test_updateQuotationDetails() {

        final String numberFolio = "14";
        final FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordEntity(numberFolio);
        final mx.com.segurossura.grouplife.domain.model.QuotationDetails quotationDetailsDomain =
                new mx.com.segurossura.grouplife.domain.model.QuotationDetails(0.2D, 0.01D, 0.2D, 0.01D, LocalDate.now(), LocalDate.now().plusYears(1), "C", AdministrationType.DETALLADA);
        final String quotationDetailsStr = quotationDetailsDomain.toString();
        final QuotationDetails quotationDetails = new QuotationDetails(
                quotationDetailsDomain.agentInitCommissionPercentage(),
                quotationDetailsDomain.promoterInitCommissionPercentage(),
                quotationDetailsDomain.agentCommissionPercentage(),
                quotationDetailsDomain.promoterCommissionPercentage(),
                quotationDetailsDomain.effectiveDate(),
                quotationDetailsDomain.effectiveEndDate(),
                quotationDetailsDomain.businessDivision(),
                quotationDetailsDomain.administrationType().toString()
        );

        folioRecordEntity.setQuotationDetails(quotationDetails);
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final UpdateQuotationDetailsRequestDto updateQuotationDetailsRequestDto = new UpdateQuotationDetailsRequestDto();
        updateQuotationDetailsRequestDto.agentCommission(0.1D);
        updateQuotationDetailsRequestDto.effectiveDate(LocalDate.now());

        final UpdateQuotationDetails200ResponseDto expect = new UpdateQuotationDetails200ResponseDto();
        expect.data(
                new StandardResponseMessageDto("success")
        );

        final UpdateQuotationDetails200ResponseDto response = this.webTestClient.patch()
                .uri(BASE_PATH, numberFolio)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updateQuotationDetailsRequestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UpdateQuotationDetails200ResponseDto.class)
                .returnResult().getResponseBody();

        assertEquals(response, expect);
        assertTrue(quotationDetailsStr.contains("DETALLADA"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateQuotationDetailsValidations")
    void test_updateQuotationDetailsValidations(final String testName, QuotationDetails quotationDetails, UpdateQuotationDetailsRequestDto updateQuotationDetailsRequestDto, StandardErrorDto errorItem) {

        final String numberFolio = "15";
        FolioRecordEntity folioRecordEntity = FolioRecordEntityUtil.createBaseFolioRecordEntity(numberFolio);
        folioRecordEntity.setQuotationDetails(quotationDetails);
        folioRecordEntity.setCompany(new Company(
                "TEST", CatalogItem.builder().key("G01").value("Giro").build(), 35,
                60, 10, new BigDecimal(1500000), new BigDecimal(1500000))
        );
        folioRecordEntity.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecordEntity).block();

        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.addErrorsItem(errorItem);

        final StandardErrorResponseDto response = this.webTestClient.patch()
                .uri(BASE_PATH, numberFolio)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updateQuotationDetailsRequestDto))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        assertEquals(expect, response);
    }

    @Test
    void test_updateQuotationDetails_FolioNotFound() {

        final String numberFolio = "16";
        final UpdateQuotationDetailsRequestDto updateQuotationDetailsRequestDto = new UpdateQuotationDetailsRequestDto();

        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.addErrorsItem(new StandardErrorDto(
                "VG-MDB-001", "FolioRecordNotFound"
        ));

        final StandardErrorResponseDto response = this.webTestClient.patch()
                .uri(BASE_PATH, numberFolio)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updateQuotationDetailsRequestDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult().getResponseBody();

        assertEquals(expect, response);
    }

}
