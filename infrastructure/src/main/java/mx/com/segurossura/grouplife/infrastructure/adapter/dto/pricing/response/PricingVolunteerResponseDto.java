package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record PricingVolunteerResponseDto(VolunteerResponse data) {

    @Builder
    public record VolunteerResponse(
            BigDecimal totalNetPremium,
            BigDecimal totalPremium,
            PeriodicResponseDto responsePeriodic,
            PaymentSurchargeResponseDto paymentSurcharge,
            FinancialDataResponseDto financialDataDocument,
            List<InsuredVolunteerResponseDto> insuredVolunteering) {
    }
}
