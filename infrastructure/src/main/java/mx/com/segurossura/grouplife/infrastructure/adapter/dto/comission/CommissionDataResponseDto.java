package mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission;

import java.time.LocalDate;

public record CommissionDataResponseDto(
        CommissionResponseDto data
) {
    public record CommissionResponseDto(
            int productKey,
            String groupKey,
            String subGroupKey,
            LocalDate effectiveDate,
            String agentKey,
            String businessDivision,
            Double commissionPercentageAgent,
            Double commissionPercentagePromoter
    ) {
    }
}
