package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CoverageResponseDto(String coverageKey, List<SumInsuredOccupationResponseDto> sumOccupation) {
}
