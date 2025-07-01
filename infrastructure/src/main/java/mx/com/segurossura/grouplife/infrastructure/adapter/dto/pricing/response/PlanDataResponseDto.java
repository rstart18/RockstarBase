package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record PlanDataResponseDto(PlanResponseListDto data) {
    public record PlanResponseListDto(List<PlanResponseDto> plans) {

    }

    @Builder
    public record PlanResponseDto(String namePlan,
                                  BigDecimal totalNetPremium,
                                  BigDecimal totalPremium,
                                  PeriodicResponseDto periodic,
                                  FinancialDataResponseDto financialData,
                                  List<PolicyHolderGroupResponseDto> policyHoldersGroups) {
    }
}