package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PeriodicResponseDto(
        BigDecimal netAnnualPremium,
        BigDecimal netSemiannualPremium,
        BigDecimal netQuarterlyPremium,
        BigDecimal netMonthlyPremium,
        BigDecimal annualPaymentSurcharge,
        BigDecimal semiannualPaymentSurcharge,
        BigDecimal quarterlyPaymentSurcharge,
        BigDecimal monthlyPaymentSurcharge,
        BigDecimal annualShippingFees,
        BigDecimal semiannualShippingFees,
        BigDecimal quarterlyShippingFees,
        BigDecimal monthlyShippingFees,
        BigDecimal totalAnnualPeriod,
        BigDecimal totalSemiannualPeriod,
        BigDecimal totalQuarterlyPeriod,
        BigDecimal totalMonthlyPeriod,
        BigDecimal firstAnnualReceipt,
        BigDecimal firstSemiannualReceipt,
        BigDecimal firstQuarterlyReceipt,
        BigDecimal firstMonthlyReceipt,
        BigDecimal subsequentAnnualReceipt,
        BigDecimal subsequentSemiannualReceipt,
        BigDecimal subsequentQuarterlyReceipt,
        BigDecimal subsequentMonthlyReceipt
) {
}
