package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

@Builder
public record InsuredSumLimitDto(SumDto min, SumDto max) {
}
