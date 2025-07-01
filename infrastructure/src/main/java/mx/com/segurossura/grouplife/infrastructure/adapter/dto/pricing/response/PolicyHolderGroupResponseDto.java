package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PolicyHolderGroupResponseDto(List<CoverageResponseDto> coverages,
                                           Integer group,
                                           String rule,
                                           BigDecimal insuredSum,
                                           Integer policyHolders,
                                           BigDecimal basicRiskPremium,
                                           BigDecimal optionalRiskPremium,
                                           BigDecimal netPremium,
                                           BigDecimal administrativeSum,
                                           BigDecimal operativeSum,
                                           BigDecimal salaryTotalNetPremiumAdmin,
                                           BigDecimal salaryTotalNetPremiumOperative,
                                           BigDecimal totalNetPremiumGroupAdmin,
                                           BigDecimal totalNetPremiumGroupOperative) {
}
