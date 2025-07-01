package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ReportQuotationDataRequestDto(
        @JsonProperty("H1") String h1,
        String businessLine,
        String suscriber,
        String folioNumber,
        String currency,
        String effectiveDate,
        String effectiveEndDate,
        String typeOfAdministration,
        String netPremium,
        String fee,
        String surcharges,
        String subtotal,
        @JsonProperty("IVA") String iva,
        String totalCost,
        String executive,
        String policyHolder,
        String typeOfRisk,
        String sami,
        String responsibleArea,
        String agent,
        List<SummaryRequestDto> sumaryTable,
        List<CoverageAndBenefitRequestDto> coverageAndBenefits,
        List<PaymentOptionRequestDto> paymentOptions
) {
}
