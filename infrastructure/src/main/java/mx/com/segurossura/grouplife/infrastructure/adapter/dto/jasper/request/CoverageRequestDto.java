package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import lombok.Builder;

@Builder
public record CoverageRequestDto(
        String coverage,
        String sumInsuredQuoted,
        String sumInsuredQuotedTotal,
        String sumInsuredTitular,
        String sumInsuredConyuge,
        String sumInsuredHermanos,
        String sumInsuredHijos,
        String sumInsuredPadres,
        String premium
) {
}
