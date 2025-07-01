package mx.com.segurossura.grouplife.infrastructure.mapper;

import mx.com.segurossura.grouplife.domain.model.validation.ValidateRFCRequest;
import mx.com.segurossura.grouplife.domain.model.validation.ValidationResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.validation.ValidationAPIResponseDto;
import mx.com.segurossura.grouplife.openapi.model.ValidateRFC200ResponseDto;
import mx.com.segurossura.grouplife.openapi.model.ValidateRFCDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ValidationAPIMapper {

    @Mapping(target = "typeLegal", source = "typeLegalId", qualifiedByName = "mapTypeLegalId")
    ValidateRFCRequest toValidateRFCRequest(Integer typeLegalId, String rfc, String name, String surname, String secondSurname, LocalDate birthdate);

    ValidationResponse toModel(ValidationAPIResponseDto validationAPIResponseDto);

    @Mapping(target = "isValid", source = "success")
    ValidateRFCDto toResponseDto(ValidationResponse validationResponse);

    @Named("mapTypeLegalId")
    default String mapTypeLegalId(Integer typeLegalId) {
        if (typeLegalId == null) return "";
        return switch (typeLegalId) {
            case 1 -> "personaFisica";
            case 2 -> "personaMoral";
            default -> "";
        };
    }

    //Validate RFC Response
    default ValidateRFC200ResponseDto toValidateRFCResponses(final ValidateRFCDto validateRFCDto) {
        final ValidateRFC200ResponseDto response = new ValidateRFC200ResponseDto();
        response.setData(validateRFCDto);
        return response;
    }
}
