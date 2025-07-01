package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

public record PaymentLinkJsonIssueRequestDto(PaymentLinkConfigurationDto configuration,
                                             PaymentLinkJsonIssuePolicyRequestDto policy,
                                             PaymentLinkClientRequestDto client,
                                             PaymentLinkFromPaymentRequestDto formPayment) {
}
