package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

public record PaymentLinkCostsRequestDto(Double amount, Double netPremium, Double policyRights,
                                         Double policySurcharges, Double netPremiumTaxes, Double policyRightsTaxes,
                                         Double discountValue, Double firstPayment, Double subsequentPayment) {
}
