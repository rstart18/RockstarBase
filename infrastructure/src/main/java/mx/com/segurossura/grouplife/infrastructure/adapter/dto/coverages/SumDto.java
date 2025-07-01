package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SumDto(
        BigDecimal defaultValue,
        DependenciesDto dependencies, String formulaDescription, String formula) {
}