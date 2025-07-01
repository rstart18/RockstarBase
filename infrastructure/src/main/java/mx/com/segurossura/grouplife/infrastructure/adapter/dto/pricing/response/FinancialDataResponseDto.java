package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinancialDataResponseDto(
        BigDecimal rightPolicy,
        BigDecimal fractionalSurcharge,
        BigDecimal vat
) {
}
