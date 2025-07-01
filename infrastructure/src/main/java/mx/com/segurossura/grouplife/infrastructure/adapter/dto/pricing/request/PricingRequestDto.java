package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PricingRequestDto(String version, String agentCommission, String promoterCommission, String sami,
                                List<PlanRequestDto> plans, List<PolicyHolderGroupRequestDto> policyHoldersGroups) {
}
