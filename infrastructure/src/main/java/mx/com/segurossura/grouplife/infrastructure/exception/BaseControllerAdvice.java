package mx.com.segurossura.grouplife.infrastructure.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.domain.model.enums.FieldNames;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class BaseControllerAdvice {

    protected abstract Map<Class<? extends RuntimeException>, HttpStatus> getHttpStatusByCodeException();

    protected abstract String getLoggerPrefix();

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handleGeneralException(final Exception exception) {
        return Mono.just(exception)
                .doOnNext(ex -> log.error("{} [handleGeneralException] Request: {}", this.getLoggerPrefix(), ex.getMessage(), ex))
                .flatMap(ex -> {
                    final List<StandardErrorDto> errorItems = new ArrayList<>();
                    final StandardErrorDto errorItem = new StandardErrorDto();
                    errorItem.setCode("VG-SERVER");
                    errorItem.setDescription("INTERNAL_SERVER_ERROR");

                    errorItems.add(errorItem);
                    final StandardErrorResponseDto errorResponse = new StandardErrorResponseDto();
                    errorResponse.setErrors(errorItems);

                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(errorResponse));
                });
    }


    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handleServerWebInputException(final ServerWebInputException exception) {
        return Mono.just(exception)
                .doOnNext(ex -> log.error("{}[handleServerWebInputException] {}", this.getLoggerPrefix(), ex.getMessage(), ex))
                .flatMap(ex -> {
                    final List<StandardErrorDto> errorItems = new ArrayList<>();
                    final StandardErrorDto errorItem = new StandardErrorDto();
                    errorItem.setCode("VG-INPUT-001");
                    errorItem.setDescription("Invalid request body");
                    errorItems.add(errorItem);
                    final StandardErrorResponseDto errorResponse = new StandardErrorResponseDto();
                    errorResponse.setErrors(errorItems);

                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(errorResponse));
                });
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handleValidationExceptions(final WebExchangeBindException ex) {
        final List<StandardErrorDto> errorItemResponseDtoList = new ArrayList<>();


        ex.getBindingResult().getFieldErrors().forEach(error ->
        {
            final StandardErrorDto errorItem = new StandardErrorDto();
            errorItem.setCode("VG-INPUT-002");
            errorItem.setDescription(this.getCustomMessage(error));
            errorItem.setField(error.getField());
            System.out.println(getCustomMessage(error));
            System.out.println(error);

            errorItemResponseDtoList.add(errorItem);
        });

        final StandardErrorResponseDto errorResponse = new StandardErrorResponseDto();
        errorResponse.setErrors(errorItemResponseDtoList);

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handleValidationExceptions(final MissingRequestValueException ex) {

        final List<StandardErrorDto> errorItemResponseDtoList = new ArrayList<>();
        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-003");
        errorItem.setDescription(ex.getReason());
        errorItem.setField(ex.getName());
        errorItemResponseDtoList.add(errorItem);
        final StandardErrorResponseDto errorResponse = new StandardErrorResponseDto();
        errorResponse.setErrors(errorItemResponseDtoList);

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handleValidationExceptions(final ConstraintViolationException ex) {
        List<StandardErrorDto> errorItemResponseDtoList = new ArrayList<>();

        ex.getConstraintViolations().forEach(violation -> {
            String dynamicField = violation.getPropertyPath().toString();
            FieldError fieldError = new FieldError(violation.getRootBeanClass().getSimpleName(), dynamicField, violation.getMessage());

            StandardErrorDto errorItem = new StandardErrorDto();
            errorItem.setCode("VG-INPUT-004");
            errorItem.setDescription(this.getCustomMessage(fieldError));
            errorItemResponseDtoList.add(errorItem);
        });

        StandardErrorResponseDto errorResponse = new StandardErrorResponseDto();
        errorResponse.setErrors(errorItemResponseDtoList);

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(GatewayException.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handlerGatewayException(final GatewayException exception) {
        return Mono.just(exception)
                .doOnNext(ex -> log.error("{} [handlerGatewayException] Request: {}", getLoggerPrefix(), ex.getMessage(), ex))
                .flatMap(codeException -> {

                    final List<StandardErrorDto> errorItemResponseDtoList = new ArrayList<>();

                    final StandardErrorDto errorItemResponseDto = new StandardErrorDto();
                    errorItemResponseDto.setCode(codeException.getCode());
                    errorItemResponseDto.setDescription(codeException.getMessage());

                    errorItemResponseDtoList.add(errorItemResponseDto);

                    final HttpStatus httpStatus = getHttpStatusByCodeException().getOrDefault(codeException.getClass(),
                            HttpStatus.BAD_GATEWAY);

                    final StandardErrorResponseDto defaultErrorResponseDto = new StandardErrorResponseDto();
                    defaultErrorResponseDto.setErrors(errorItemResponseDtoList);

                    return Mono.just(new ResponseEntity<>(defaultErrorResponseDto, httpStatus));
                });
    }

    @ExceptionHandler(FolioRecordException.FolioRecordNotFound.class)
    public Mono<ResponseEntity<StandardErrorResponseDto>> handleFolioRecordException(final FolioRecordException.FolioRecordNotFound folioRecordException) {
        return Mono.just(folioRecordException)
                .doOnNext(ex -> log.error("{} [handleFolioRecordException] Request: {}", this.getLoggerPrefix(), ex.getMessage(), ex))
                .flatMap(ex -> {

                    final List<StandardErrorDto> errorItems = new ArrayList<>();
                    final StandardErrorDto errorItem = new StandardErrorDto();
                    errorItem.setCode(folioRecordException.getMessage());
                    errorItem.setDescription(folioRecordException.getClass().getSimpleName());

                    errorItems.add(errorItem);

                    final StandardErrorResponseDto errorResponse = new StandardErrorResponseDto();
                    errorResponse.setErrors(errorItems);

                    return Mono.just(ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(errorResponse));
                });
    }

    private String getCustomMessage(FieldError error) {
        return switch (error.getCode()) {
            case "AssertFalse" -> "debe ser falso.";
            case "AssertTrue" -> "debe ser verdadero.";
            case "DecimalMax" -> "debe ser menor o igual a " + Objects.requireNonNull(error.getArguments())[1] + ".";
            case "DecimalMin" -> "debe ser mayor o igual a " + Objects.requireNonNull(error.getArguments())[1] + ".";
            case "Digits" ->
                    "debe tener hasta " + Objects.requireNonNull(error.getArguments())[1] + " dígitos enteros y " + Objects.requireNonNull(error.getArguments())[2] + " decimales.";
            case "Email" -> "debe ser una dirección de correo válida.";
            case "Future" -> "debe ser una fecha en el futuro.";
            case "FutureOrPresent" -> "debe ser una fecha en el presente o en el futuro.";
            case "Max" -> "debe ser menor o igual a " + Objects.requireNonNull(error.getArguments())[1] + ".";
            case "Min" -> "debe ser mayor o igual a " + Objects.requireNonNull(error.getArguments())[1] + ".";
            case "Negative" -> "debe ser un número negativo.";
            case "NegativeOrZero" -> "debe ser un número negativo o cero.";
            case "NotBlank" -> "no debe estar en blanco.";
            case "NotEmpty" -> "no debe estar vacío.";
            case "NotNull" -> "no debe ser nulo.";
            case "Null" -> "debe ser nulo.";
            case "Past" -> "debe ser una fecha en el pasado.";
            case "PastOrPresent" -> "debe ser una fecha en el pasado o presente.";
            case "Pattern" -> {
                String fieldName = error.getField().substring(error.getField().lastIndexOf('.') + 1);
                String translatedField = FieldNames.getSpanishName(fieldName);
                yield translatedField + " no cumple con el formato requerido.";
            }
            case "Positive" -> "debe ser un número positivo.";
            case "PositiveOrZero" -> "debe ser un número positivo o cero.";
            case "Size" ->
                    "debe tener entre " + Objects.requireNonNull(error.getArguments())[2] + " y " + Objects.requireNonNull(error.getArguments())[1] + " caracteres.";
            case null -> "No validation";
            default -> error.getDefaultMessage();
        };
    }
}
