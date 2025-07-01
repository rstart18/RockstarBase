package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class WebClientErrorHandlingUtils {

    // Constructor privado para evitar la creación de instancias de la clase
    private WebClientErrorHandlingUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse
                -> {
            if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            final String errorDetails = String.format("Unauthorized access: Status %s, details: %s",
                                    clientResponse.statusCode(), errorBody);
                            return Mono.error(new GatewayException.GatewayUnauthorizedException(errorDetails));
                        });
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            final String errorDetails = String.format("Client error: Status %s, details: %s",
                                    clientResponse.statusCode(), errorBody);
                            return Mono.error(new GatewayException.GatewayClientErrorException(errorDetails));
                        });
            } else if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            final String errorDetails = String.format("Server error: Status %s, details: %s",
                                    clientResponse.statusCode(), errorBody);
                            return Mono.error(new GatewayException.GatewayServerErrorException(errorDetails));
                        });
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

}
