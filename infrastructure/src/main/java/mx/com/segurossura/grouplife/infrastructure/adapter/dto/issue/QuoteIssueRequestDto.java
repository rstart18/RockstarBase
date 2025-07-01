package mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue;

import lombok.Builder;

@Builder
public record QuoteIssueRequestDto(String folio,
                                   String periodicityPaymentId,
                                   String paymentMethodId) {
}
