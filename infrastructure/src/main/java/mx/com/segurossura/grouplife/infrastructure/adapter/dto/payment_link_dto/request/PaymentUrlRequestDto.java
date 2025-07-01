package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

public record PaymentUrlRequestDto(PaymentLinkConfigurationDto configuration,
                                   PaymentLinkUserRequestDto user, PaymentLinkPolicyRequestDto policy,
                                   PaymentLinkJsonIssueRequestDto jsonIssue, PaymentLinkInfoClient infoClient,
                                   boolean toClient) {
}
