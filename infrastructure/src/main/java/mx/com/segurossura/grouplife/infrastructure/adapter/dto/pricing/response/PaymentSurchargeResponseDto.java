package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentSurchargeResponseDto(
        BigDecimal monthlySurcharge,
        BigDecimal quarterlySurcharge,
        BigDecimal semiannualSurcharge,
        BigDecimal annualSurcharge
) {
}
