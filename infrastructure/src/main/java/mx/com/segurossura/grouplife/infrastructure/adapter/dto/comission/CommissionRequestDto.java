package mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission;

import java.time.LocalDate;

public record CommissionRequestDto(
        int productKey,
        String groupKey,
        String subGroupKey,
        LocalDate effectiveDate,
        String agentKey,
        String businessDivision
) {
}
