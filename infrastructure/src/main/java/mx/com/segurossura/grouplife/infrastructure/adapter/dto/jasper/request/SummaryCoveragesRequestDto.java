package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

public record SummaryCoveragesRequestDto(
        String coverage,
        String minNewRegistrations,
        String newRegistrationsRenewals,
        String cancellation
) {
}
