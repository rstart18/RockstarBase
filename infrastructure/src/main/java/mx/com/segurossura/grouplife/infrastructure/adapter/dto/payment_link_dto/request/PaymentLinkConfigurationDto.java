package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

import lombok.Builder;

@Builder
public record PaymentLinkConfigurationDto(String partnerId, String product, Long folioFromPartner, Long folio,
                                          String channel) {
}
