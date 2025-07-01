package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import lombok.Builder;

import java.util.List;

@Builder
public record SummaryRequestDto(
        String coverage,
        String minNewRegistrations,
        String maxNewRegistrations,
        String newRegistrationsRenewals,
        String cancellation,
        String agesAcceptanceTitle,
        List<SummaryCoveragesRequestDto> coverages
) {
}
