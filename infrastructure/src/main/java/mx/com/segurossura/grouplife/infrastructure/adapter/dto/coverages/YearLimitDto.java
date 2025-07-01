package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

@Builder
public record YearLimitDto(int value, String unit) {
}
