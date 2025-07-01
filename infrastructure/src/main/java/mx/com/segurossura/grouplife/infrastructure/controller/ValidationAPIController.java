package mx.com.segurossura.grouplife.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.service.ValidationAPIService;
import mx.com.segurossura.grouplife.domain.model.validation.ValidateRFCRequest;
import mx.com.segurossura.grouplife.infrastructure.mapper.ValidationAPIMapper;
import mx.com.segurossura.grouplife.openapi.api.ValidateRfcApi;
import mx.com.segurossura.grouplife.openapi.model.TypeLegalIdDto;
import mx.com.segurossura.grouplife.openapi.model.ValidateRFC200ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ValidationAPIController implements ValidateRfcApi {

    private static final String LOGGER_PREFIX = String.format("[%s] ", ValidationAPIController.class.getSimpleName());
    private final ValidationAPIService validationAPIService;
    private final ValidationAPIMapper validationAPIMapper;

    @Override
    public Mono<ResponseEntity<ValidateRFC200ResponseDto>> validateRFC(Integer typeLegalId, String rfc, String name, LocalDate birthdate, String surname, String secondSurname, ServerWebExchange exchange) {
        return this.validationAPIService
                .validateRfc(this.validationAPIMapper.toValidateRFCRequest(
                        typeLegalId, rfc, name, surname, secondSurname, birthdate)
                )
                .map(this.validationAPIMapper::toResponseDto)
                .map(this.validationAPIMapper::toValidateRFCResponses)
                .map(ResponseEntity::ok)
                .doOnTerminate(() -> log.info("{}[validateRFC] Completed", LOGGER_PREFIX));
    }

}
