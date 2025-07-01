package mx.com.segurossura.grouplife.gateway;

import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog.CatalogListDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.gateway.CatalogGateway;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import mx.com.segurossura.grouplife.openapi.model.CatalogItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.when;

public class CatalogGatewayTest {

    @Mock
    private WebClient catalogWebClient;
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

    @Mock
    private CatalogMapper catalogMapper;

    @InjectMocks
    private CatalogGateway catalogGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.catalogWebClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestHeadersSpec);
        when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    }

    @Test
    void getBusinessActivity_Success() {
        // Mock de la respuesta del WebClient
        CatalogItemDto catalogItemDto = new CatalogItemDto("00001", "COVERAGE");
        CatalogListDto catalogListDto = new CatalogListDto(List.of(catalogItemDto));

        when(this.responseSpec.bodyToMono(CatalogListDto.class)).thenReturn(Mono.just(catalogListDto));
        when(this.catalogMapper.toModel(catalogItemDto)).thenReturn(new CatalogItem("00001", "COVERAGE"));

        // Verificación
        StepVerifier.create(catalogGateway.getBusinessActivity())
                .expectNextMatches(item -> item.key().equals("00001"))
                .verifyComplete();
    }

    @Test
    void getBusinessActivity_NoActivities() {

        CatalogListDto catalogListDto = new CatalogListDto(null);

        when(responseSpec.bodyToMono(CatalogListDto.class)).thenReturn(Mono.just(catalogListDto));

        StepVerifier.create(catalogGateway.getBusinessActivity())
                .expectErrorMatches(throwable -> throwable instanceof GatewayException &&
                        throwable.getMessage().contains("No business activities found"))
                .verify();
    }

}
