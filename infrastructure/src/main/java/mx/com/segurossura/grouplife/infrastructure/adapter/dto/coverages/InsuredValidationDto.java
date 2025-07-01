package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

import java.util.List;

@Builder
public record InsuredValidationDto(Boolean noValidateAge, String kinshipKey, String kinship, AgeLimitDto acceptableYearOldLimit,
                                   AgeLimitDto renovationYearOldLimit, AgeLimitDto cancellationYearOldLimit,
                                   List<InsuredSumLimitDto> insuredSumLimit
) {
}
