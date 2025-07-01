package mx.com.segurossura.grouplife.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import mx.com.segurossura.grouplife.domain.model.mailpolicy.MailPolicyRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailpolicy.MailPolicyRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailpolicy.MailPolicyResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.MailPolicyGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MailPolicyGatewayTest {

    private final MailPolicyMapper mapper = Mappers.getMapper(MailPolicyMapper.class);
    @Mock
    private WebClient mailPolicyWebClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;
    @InjectMocks
    private MailPolicyGateway mailPolicyGateway;
    @Mock
    private MailPolicyMapper mailPolicyMapper;
    @Mock
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mailPolicyWebClient.post()).thenReturn(mock(WebClient.RequestBodyUriSpec.class));
        when(mailPolicyWebClient.post().bodyValue(any())).thenReturn(mock(WebClient.RequestHeadersSpec.class));
        when(mailPolicyWebClient.post().bodyValue(any()).retrieve()).thenReturn(responseSpec);

        // Crea un Retry falso que no haga nada (maxAttempts = 1)
        Retry retry = Retry.of("testRetry", RetryConfig.custom().maxAttempts(1).build());
        ReflectionTestUtils.setField(mailPolicyGateway, "retry", retry);
    }

    @Test
    void getPDFConsolidated_Success() {

        final MailPolicyRequest mailPolicyRequest = createMailPolicyRequest();

        final MailPolicyRequestDto mailPolicyRequestDto = createMailPolicyRequestDto();

        final MailPolicyResponseDto mailPolicyResponseDto = new MailPolicyResponseDto(new MailPolicyResponseDto.Data("OK"));

        // Configura el mock de CircuitBreaker para permitir la llamada
        when(circuitBreaker.tryAcquirePermission()).thenReturn(true);

        // Configura el mock de WebClient para devolver una respuesta exitosa
        when(responseSpec.bodyToMono(MailPolicyResponseDto.class)).thenReturn(Mono.just(mailPolicyResponseDto));

        when(mailPolicyMapper.mailPolicyRequestToDto(mailPolicyRequest)).thenReturn(mailPolicyRequestDto);

        // Verificación
        StepVerifier.create(mailPolicyGateway.sendMailPolicy(mailPolicyRequest))
                .expectNextMatches(s -> s.equalsIgnoreCase("OK"))
                .verifyComplete();
    }

    @Test
    void mailPolicyRequestToDto_Success() {
        // When: Realizamos el mapeo con el mapper
        MailPolicyRequest mailPolicyRequest = createMailPolicyRequest();
        MailPolicyRequestDto mailPolicyRequestDto = mapper.mailPolicyRequestToDto(mailPolicyRequest);

        // Then: Verificamos que los campos se mapearon correctamente
        assertEquals(mailPolicyRequestDto.poliza(), mailPolicyRequest.poliza());
        assertEquals(mailPolicyRequestDto.email(), mailPolicyRequest.email());

    }

    private MailPolicyRequest createMailPolicyRequest() {
        return new MailPolicyRequest("1", "902", "123", "0", "",
                "M", "prueba@gmail.com", List.of("prueba2@gmail.com"));
    }

    private MailPolicyRequestDto createMailPolicyRequestDto() {
        return new MailPolicyRequestDto("1", "902", "123", "0", "",
                "M", "prueba@gmail.com", List.of("prueba2@gmail.com"));
    }

}
