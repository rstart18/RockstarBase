package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.ValidationAPIPort;
import mx.com.segurossura.grouplife.domain.model.validation.ValidateRFCRequest;
import mx.com.segurossura.grouplife.domain.model.validation.ValidationResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.validation.ValidationAPIResponseDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import mx.com.segurossura.grouplife.infrastructure.mapper.ValidationAPIMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationAPIGateway implements ValidationAPIPort {

    private final ValidationAPIMapper validationAPIMapper;
    private final ObjectMapper objectMapper;

    @Qualifier("validationAPIWebClient")
    private final WebClient validationAPIWebClient;

    @Override
    public Mono<ValidationResponse> validateRfc(ValidateRFCRequest validateRFCRequest) {

        return this.validationAPIWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/validate-rfc")
                        .queryParam("tipoPersona", validateRFCRequest.typeLegal())
                        .queryParam("RFC", validateRFCRequest.rfc())
                        .queryParam("nombre", validateRFCRequest.name())
                        .queryParam("apePaterno", validateRFCRequest.surname())
                        .queryParam("apeMaterno", validateRFCRequest.secondSurname())
                        .queryParam("fechaNacimiento", validateRFCRequest.birthdate())
                        .build()
                )
                .retrieve()
                .bodyToMono(ValidationAPIResponseDto.class)
                .onErrorResume(WebClientResponseException.class, ex -> {

                    String errorBody = ex.getResponseBodyAsString();
                    int statusCode = ex.getStatusCode().value();

                    if (ex.getStatusCode().is4xxClientError()) {

                        if (statusCode == 401) {
                            final String errorDetails = String.format("Unauthorized access: Status %s, details: %s", statusCode, errorBody);
                            return Mono.error(new GatewayException.GatewayUnauthorizedException(errorDetails));
                        }

                        try {
                            ValidationAPIResponseDto validationAPIResponseDto = this.objectMapper.readValue(ex.getResponseBodyAsString(), ValidationAPIResponseDto.class);
                            if (validationAPIResponseDto.renderMessage() != null
                                    && validationAPIResponseDto.renderMessage().body() != null
                                    && validationAPIResponseDto.renderMessage().body().equals("El RFC capturado no es válido")
                            ) {
                                return Mono.just(validationAPIResponseDto);
                            } else {
                                final String errorDetails = String.format("Client error: Status %s, details: %s", statusCode, errorBody);
                                return Mono.error(new GatewayException.GatewayClientErrorException(errorDetails));
                            }
                        } catch (JsonProcessingException e) {
                            final String errorDetails = String.format("Client error: Status %s, details: %s", statusCode, errorBody);
                            return Mono.error(new GatewayException.GatewayClientErrorException(errorDetails));
                        }

                    } else {
                        final String errorDetails = String.format("Server error: Status %s, details: %s", statusCode, errorBody);
                        return Mono.error(new GatewayException.GatewayServerErrorException(errorDetails));
                    }
                })
                .map(this.validationAPIMapper::toModel)
                .doOnError(e -> log.error("Error in ValidationGateway | Validate RFC: {}", e.getMessage()));
    }

}
