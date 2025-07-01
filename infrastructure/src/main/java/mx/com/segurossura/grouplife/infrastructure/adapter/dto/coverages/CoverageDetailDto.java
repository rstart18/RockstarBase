package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

import java.util.List;

@Builder
public record CoverageDetailDto(String coverageKey, String code, String description, String typeCoverage,
                                DefaultValueDto defaultValue, Boolean mandatory,
                                List<InsuredValidationDto> insuredValidations, DisplayDto display,
                                GroupedDto grouped, boolean insuredSumFix) {
}
