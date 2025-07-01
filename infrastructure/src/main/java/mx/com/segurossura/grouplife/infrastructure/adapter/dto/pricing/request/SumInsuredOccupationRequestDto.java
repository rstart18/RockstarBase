package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SumInsuredOccupationRequestDto(BigDecimal insuredSum, String occupation) {
}
