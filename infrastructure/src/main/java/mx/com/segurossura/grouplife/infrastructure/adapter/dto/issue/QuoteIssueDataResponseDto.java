package mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue;

import lombok.Builder;

@Builder
public record QuoteIssueDataResponseDto(QuoteIssueResponse data) {

    public record QuoteIssueResponse(String folio,
                       String requestId,
                       String message) {
    }
}
