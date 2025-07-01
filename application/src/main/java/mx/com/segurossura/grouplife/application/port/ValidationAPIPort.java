package mx.com.segurossura.grouplife.application.port;

import mx.com.segurossura.grouplife.domain.model.validation.ValidateRFCRequest;
import mx.com.segurossura.grouplife.domain.model.validation.ValidationResponse;
import reactor.core.publisher.Mono;

public interface ValidationAPIPort {

    Mono<ValidationResponse> validateRfc(ValidateRFCRequest validateRFCRequest);
}
