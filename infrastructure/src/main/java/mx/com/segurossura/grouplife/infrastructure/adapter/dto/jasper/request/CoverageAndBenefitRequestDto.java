package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import lombok.Builder;

import java.util.List;

@Builder
public record CoverageAndBenefitRequestDto(
        String nameOfSubgroup,
        String numberOfInsured,
        String occupation,
        String total,
        String note,
        String sumInsuredRule,
        List<CoverageRequestDto> coverages
) {
}
