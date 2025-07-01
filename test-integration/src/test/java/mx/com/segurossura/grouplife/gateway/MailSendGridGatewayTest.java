package mx.com.segurossura.grouplife.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailsendgrid.MailSendGridResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.MailSendGridGateway;
import mx.com.segurossura.grouplife.infrastructure.configuration.MailNotificationProperties;
import mx.com.segurossura.grouplife.infrastructure.configuration.MailSendGridProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MailSendGridGatewayTest {

    Retry retry;
    @Mock
    private WebClient mailSendGridWebClient;
    @Mock
    private CircuitBreaker circuitBreaker;
    @Mock
    private MailSendGridProperties mailSendGridProperties;
    @Mock
    private MailNotificationProperties mailNotificationProperties;
    @InjectMocks
    private MailSendGridGateway mailSendGridGateway;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inyecta el valor de agentPortalUrl usando ReflectionTestUtils
        ReflectionTestUtils.setField(mailSendGridGateway, "agentPortalUrl", "http://example.com");

        // Crea un Retry falso que no haga nada (maxAttempts = 1)
        Retry retry = Retry.of("testRetry", RetryConfig.custom().maxAttempts(1).build());
        ReflectionTestUtils.setField(mailSendGridGateway, "retry", retry);
    }

    @Test
    public void testSendNotificationMail_Success() {
        // Configura el mock de WebClient para devolver una respuesta exitosa
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(mailSendGridWebClient.post()).thenReturn(mock(WebClient.RequestBodyUriSpec.class));
        when(mailSendGridWebClient.post().bodyValue(any())).thenReturn(mock(WebClient.RequestHeadersSpec.class));
        when(mailSendGridWebClient.post().bodyValue(any()).retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MailSendGridResponse.class)).thenReturn(Mono.just(new MailSendGridResponse(true, "Success", new Object(), "1")));

        // Configura el mock de CircuitBreaker para permitir la llamada
        when(circuitBreaker.tryAcquirePermission()).thenReturn(true);

        // Configura el mock de MailNotificationProperties
        MailNotificationProperties.Success success = new MailNotificationProperties.Success();
        success.setImg("url_imagen_exito");
        success.setTitle("Éxito");
        success.setTitleColor("#00FF00");
        success.setBodyCardTraditional("Terminamos de emitir tu solicitud de Vida Grupo con el <strong>número de folio {{FOLIO}}</strong>. Continua con el proceso de pago desde el portal de agente.");
        success.setBodyLinkTraditional("Terminamos de emitir tu solicitud de Vida Grupo con el <strong>número de folio {{FOLIO}}</strong>. Continua con el proceso de pago desde el portal de agente.");
        success.setBodyTransferTraditional("Terminamos de emitir tu solicitud de Vida Grupo con el <strong>número de folio {{FOLIO}}</strong>. Continua con el proceso de pago desde el portal de agente.");

        MailNotificationProperties.Error error = new MailNotificationProperties.Error();
        error.setImg("url_imagen_error");
        error.setTitle("Error");
        error.setTitleColor("#FF0000");
        error.setBodyTraditional("Hubo un problema con tu solicitud de Vida Grupo con el <strong>número de folio {{FOLIO}}</strong>. Por favor, inténtalo de nuevo.");

        when(mailNotificationProperties.getSuccess()).thenReturn(success);
        when(mailNotificationProperties.getError()).thenReturn(error);
        when(mailNotificationProperties.getSubject()).thenReturn("Continua con la compra de tu seguro de vida");

        // Ejecuta el método bajo prueba
        Mono<String> result = mailSendGridGateway.sendNotificationMail("test@example.com", "12345", true, 3, true, Modality.TRADITIONAL);

        // Verifica el resultado
        StepVerifier.create(result)
                .expectNext("Success")
                .verifyComplete();
    }

}