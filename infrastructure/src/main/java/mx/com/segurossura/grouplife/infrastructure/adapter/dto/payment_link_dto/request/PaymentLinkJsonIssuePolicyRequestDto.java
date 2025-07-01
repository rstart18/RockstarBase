package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

import lombok.Builder;

@Builder
public record PaymentLinkJsonIssuePolicyRequestDto(int officeId, String currencyId, String atrGroupId,
                                                   String atrSubgroupId, String atrProfileRateId,
                                                   String atrPointSaleId, String userId) {
}
