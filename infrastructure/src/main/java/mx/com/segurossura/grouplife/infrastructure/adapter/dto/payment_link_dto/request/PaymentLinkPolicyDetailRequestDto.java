package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

public record PaymentLinkPolicyDetailRequestDto(Integer officeId, Integer productId, String status, Integer policyNumber,
                                                long folioFromPartner) {
}
