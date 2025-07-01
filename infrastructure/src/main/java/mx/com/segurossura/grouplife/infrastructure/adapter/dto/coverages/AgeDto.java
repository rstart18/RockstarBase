package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

@Builder
public record AgeDto(Integer min, Integer max, Integer averageMin, Integer averageMax, Integer averageDefault) {
}
