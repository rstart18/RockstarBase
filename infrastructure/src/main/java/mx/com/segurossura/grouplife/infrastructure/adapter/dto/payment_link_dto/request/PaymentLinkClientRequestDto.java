package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

public record PaymentLinkClientRequestDto(PaymentLinkPersonalDataRequestDto personalData,
                                          PaymentLinkAddressRequestDto address) {
}
