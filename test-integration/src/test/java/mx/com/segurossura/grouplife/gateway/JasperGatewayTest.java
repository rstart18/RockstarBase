package mx.com.segurossura.grouplife.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import mx.com.segurossura.grouplife.domain.model.jasper.Coverage;
import mx.com.segurossura.grouplife.domain.model.jasper.CoverageAndBenefit;
import mx.com.segurossura.grouplife.domain.model.jasper.DirectionsElements;
import mx.com.segurossura.grouplife.domain.model.jasper.PaymentOption;
import mx.com.segurossura.grouplife.domain.model.jasper.ReportQuotation;
import mx.com.segurossura.grouplife.domain.model.jasper.ReportQuotationData;
import mx.com.segurossura.grouplife.domain.model.jasper.Summary;
import mx.com.segurossura.grouplife.domain.model.jasper.SummaryCoverages;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.CoverageAndBenefitRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.CoverageRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.DirectionsElementsRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.PaymentOptionRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.ReportQuotationDataRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.ReportQuotationRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.SummaryCoveragesRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request.SummaryRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.response.JasperDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.JasperGateway;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JasperGatewayTest {

    private final JasperQuotationMapper mapper = Mappers.getMapper(JasperQuotationMapper.class);
    @Mock
    private WebClient jasperWebClient;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @InjectMocks
    private JasperGateway jasperGateway;
    @Mock
    private JasperQuotationMapper jasperQuotationMapper;
    @Mock
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crea un Retry falso que no haga nada (maxAttempts = 1)
        Retry retry = Retry.of("folioSequenceRetry", RetryConfig.custom().maxAttempts(1).build());
        ReflectionTestUtils.setField(jasperGateway, "folioSequenceRetry", retry);
    }

    //@Test
    void getPDFBase64_data_null() {
        final JasperDataResponseDto jasperDataResponseDto = new JasperDataResponseDto(null);

        final ReportQuotation reportQuotation = createReportQuotation();
        final ReportQuotationRequestDto reportQuotationRequestDto = createReportQuotationRequestDto();

        // Configura el mock de WebClient para devolver una respuesta exitosa
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(jasperWebClient.post()).thenReturn(mock(WebClient.RequestBodyUriSpec.class));
        when(jasperWebClient.post().bodyValue(any())).thenReturn(mock(WebClient.RequestHeadersSpec.class));
        when(jasperWebClient.post().bodyValue(any()).retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JasperDataResponseDto.class)).thenReturn(Mono.just(jasperDataResponseDto));

        // Configura el mock de CircuitBreaker para permitir la llamada
        when(circuitBreaker.tryAcquirePermission()).thenReturn(true);

        // Configura el mock de WebClient para devolver una respuesta exitosa
        //when(responseSpec.bodyToMono(JasperDataResponseDto.class)).thenReturn(Mono.just(jasperDataResponseDto));

        when(jasperQuotationMapper.modelToRequest(reportQuotation)).thenReturn(reportQuotationRequestDto);

        StepVerifier.create(jasperGateway.getPDFBase64(reportQuotation))
                .expectErrorMatches(throwable -> throwable instanceof GatewayException &&
                        ((GatewayException) throwable).getCode().contains("VG-GTW-NULL-JASPER"))
                .verify();
    }

    private ReportQuotation createReportQuotation() {
        final SummaryCoverages summaryCoverages = new SummaryCoverages("xxx", "xxx", "xxx", "xxx");

        final Summary summary = new Summary("xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                List.of(summaryCoverages));

        final Coverage coverage = new Coverage("xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx");

        final CoverageAndBenefit coverageAndBenefit = new CoverageAndBenefit("xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", List.of(coverage));

        final PaymentOption paymentOption = new PaymentOption("xxx", "xxx", "xxx", "xxx", "xxx");

        final ReportQuotationData reportQuotationData = new ReportQuotationData("xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                List.of(summary), List.of(coverageAndBenefit), List.of(paymentOption));

        final DirectionsElements directionsElements = new DirectionsElements("xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx");

        final ReportQuotation.Parametros parametros = new ReportQuotation.Parametros("xxx", "ruta1/ruta2");

        return new ReportQuotation(new ReportQuotation.Reportes(reportQuotationData, parametros, "subreport_vidagrupo"),
                directionsElements);
    }

    private ReportQuotationRequestDto createReportQuotationRequestDto() {
        final SummaryCoveragesRequestDto summaryCoverages = new SummaryCoveragesRequestDto("xxx", "xxx", "xxx", "xxx");

        final SummaryRequestDto summary = new SummaryRequestDto("xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                List.of(summaryCoverages));

        final CoverageRequestDto coverage = new CoverageRequestDto("xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx");

        final CoverageAndBenefitRequestDto coverageAndBenefit = new CoverageAndBenefitRequestDto("xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", List.of(coverage));

        final PaymentOptionRequestDto paymentOption = new PaymentOptionRequestDto("xxx", "xxx", "xxx", "xxx", "xxx");

        final ReportQuotationDataRequestDto reportQuotationData = new ReportQuotationDataRequestDto("xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                List.of(summary), List.of(coverageAndBenefit), List.of(paymentOption));

        final DirectionsElementsRequestDto directionsElements = new DirectionsElementsRequestDto("xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx",
                "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx", "xxx");

        final ReportQuotationRequestDto.Parametros parametros = new ReportQuotationRequestDto.Parametros("xxx", "ruta1/ruta2");

        return new ReportQuotationRequestDto(new ReportQuotationRequestDto.Reportes(reportQuotationData, parametros, "subreport_vidagrupo"),
                directionsElements);
    }

}
