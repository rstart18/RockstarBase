package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanRequestDto(String namePlan, List<PolicyHolderGroupRequestDto> policyHoldersGroups) {
}
