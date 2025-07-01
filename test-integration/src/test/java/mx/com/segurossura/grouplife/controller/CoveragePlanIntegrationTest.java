package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog.CoveragePlanDataGtwDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.openapi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoveragePlanIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/plans";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    @Mock
    ClientResponse clientResponse;

    @Mock
    ExchangeFunction exchangeFilterFunction;

    @MockitoBean
    @Qualifier("catalogWebClient")
    private WebClient catalogWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setup() {
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);

        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
        when(this.exchangeFilterFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(this.clientResponse));
    }

    @Test
    void test_getCoveragePlans() {
        final YearLimitDto min = new YearLimitDto();
        min.setUnit("YEAR");
        min.setValue(15);
        final YearLimitDto max = new YearLimitDto();
        max.setUnit("YEAR");
        max.setValue(69);
        AgeLimitDto ageLimitDto = new AgeLimitDto();
        ageLimitDto.min(min);
        ageLimitDto.max(max);
        SumDto minSum = new SumDto();
        minSum.setFormula("formula");
        minSum.setDefaultValue(new BigDecimal(500000));
        minSum.dependencies(null);
        minSum.formulaDescription("formula");
        SumDto maxSum = new SumDto();
        maxSum.setFormula("formula");
        maxSum.setDefaultValue(new BigDecimal(500000));
        maxSum.dependencies(null);
        maxSum.formulaDescription("formula");
        final InsuredSumLimitDto insuredSumLimitDto = new InsuredSumLimitDto();
        insuredSumLimitDto.setMax(maxSum);
        insuredSumLimitDto.setMin(minSum);
        final InsuredValidationDto insuredValidationDto = new InsuredValidationDto();
        insuredValidationDto.setKinship(null);
        insuredValidationDto.setKinshipKey(null);
        insuredValidationDto.setAcceptableYearOldLimit(ageLimitDto);
        insuredValidationDto.setInsuredSumLimit(List.of(insuredSumLimitDto));

        final CoverageDetailDisplayDto coverageDetailDisplayDto = new CoverageDetailDisplayDto();
        coverageDetailDisplayDto.setColor("COLOR");
        coverageDetailDisplayDto.setType("TYPE");

        final CoverageDetailGroupedDto coverageDetailGroupedDto = new CoverageDetailGroupedDto();
        coverageDetailGroupedDto.setText("TEXT");
        coverageDetailGroupedDto.setSiblingCoverages(List.of());
        coverageDetailGroupedDto.setTitle("TITLE");

        final CoverageDetailDto coverageDetailDto = new CoverageDetailDto();
        coverageDetailDto.setCoverageKey("FALLECIMIENTO");
        coverageDetailDto.setDescription("Fallecimiento");
        coverageDetailDto.setCode("00001");
        coverageDetailDto.setTypeCoverage("BASICA");
        coverageDetailDto.setMandatory(true);
        coverageDetailDto.setDefaultValue(null);
        coverageDetailDto.setInsuredValidations(List.of(insuredValidationDto));
        coverageDetailDto.setInsuredSumFix(false);
        coverageDetailDto.setDisplay(coverageDetailDisplayDto);
        coverageDetailDto.setGrouped(coverageDetailGroupedDto);

        final CoveragePlanItemDto coveragePlanItemDto = new CoveragePlanItemDto();
        coveragePlanItemDto.setCode("00001");
        coveragePlanItemDto.setDescription("DESCRIPTION");
        coveragePlanItemDto.setCoverages(List.of(coverageDetailDto));

        final PlanDtoDto planDtoDto = new PlanDtoDto();
        planDtoDto.setPlanKey("ESTANDAR");
        planDtoDto.setCoverages(List.of(coveragePlanItemDto));
        planDtoDto.setPlanDescription("estandar");
        planDtoDto.suggestion(true);
        planDtoDto.setVignette("vignette");
        final CoveragePlans200ResponseDto expected = new CoveragePlans200ResponseDto();
        expected.setData(List.of(planDtoDto));

        final CoveragePlanDataGtwDto mockDto = TestFixtures.createCoveragePlanDataGtwDto();
        when(this.responseSpec.bodyToMono(CoveragePlanDataGtwDto.class)).thenReturn(Mono.just(mockDto));

        final InsuranceDataDto mnockCoverage = TestFixtures.createInsuranceDataDto();
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mnockCoverage));

        final CoveragePlans200ResponseDto actualResponse =
                this.webTestClient.get()
                        .uri(BASE_PATH + "?modality=TRADICIONAL")
                        .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(CoveragePlans200ResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(expected, actualResponse);

        final Function<UriBuilder, URI> capturedUriFunction = this.uriCaptor.getValue();
        final UriBuilder uriBuilderMock = mock(UriBuilder.class);

        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(anyString(), anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(URI.create("/catalogs/902?catalog=COVERAGE&key=PLANS_COVERAGE"));

        capturedUriFunction.apply(uriBuilderMock);
        verify(uriBuilderMock).path("/catalogs/902");
        verify(uriBuilderMock).queryParam("catalog", "COVERAGE");
        //verify(uriBuilderMock).queryParam("key", "PLANS_COVERAGE");
    }

    @Test
    void test_getCoveragePlans_shouldReturnError_whenServerError() {

        final CoveragePlanDataGtwDto mockDto = new CoveragePlanDataGtwDto(null);
        when(this.responseSpec.bodyToMono(CoveragePlanDataGtwDto.class)).thenReturn(Mono.just(mockDto));

        final InsuranceDataDto mnockCoverage = new InsuranceDataDto(null);
        when(this.responseSpec.bodyToMono(InsuranceDataDto.class)).thenReturn(Mono.just(mnockCoverage));

        final StandardErrorResponseDto actualResponse = this.webTestClient.get()
                .uri(BASE_PATH + "?modality=TRADICIONAL")
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-GTW-NO-COVERAGES");
        errorItem.setDescription("No coverages found");
        final StandardErrorResponseDto expected = new StandardErrorResponseDto();
        expected.errors(Collections.singletonList(errorItem));

        assertNotNull(actualResponse);
        assertEquals(actualResponse, expected);
    }
}
