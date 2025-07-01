package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InsuredVolunteerResponseDto(List<CoverageResponseDto> coverages,
                                          Integer numberGroup,
                                          Integer policyHolders,
                                          List<FamilyResponseDto> families,
                                          List<SumNetPremiumResponseDto> sumNetPremium) {
}
