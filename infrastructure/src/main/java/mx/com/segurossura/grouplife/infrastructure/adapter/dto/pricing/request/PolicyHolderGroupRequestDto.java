package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PolicyHolderGroupRequestDto(String rule, Integer group, List<CoverageRequestDto> coverages, List<PolicyHolderRequestDto> policyHolders) {
}
