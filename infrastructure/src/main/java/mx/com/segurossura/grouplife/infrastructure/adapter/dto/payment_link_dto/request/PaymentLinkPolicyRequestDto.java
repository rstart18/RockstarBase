package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PaymentLinkPolicyRequestDto(String currency, String formPayment, String periodicity,
                                          PaymentLinkPolicyDetailRequestDto quote,
                                          List<PaymentLinkPolicyDetailRequestDto> issue,
                                          PaymentLinkCostsRequestDto costs) {
}
