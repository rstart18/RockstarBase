package mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue;

import java.time.LocalDateTime;

public record StatusFolioDataResponse(
        StatusFolio data
) {
    public record StatusFolio(
            Policy policy, Costs costs, String requestId, String jobName,
            String folio, LocalDateTime requestTime, LocalDateTime startTime, LocalDateTime endTime,
            String status, String comments
    ) {
        public record Policy(
                Integer officeId, Integer productId, String status, Long policyNumber
        ) {
        }

        public record Costs(
                Double amount, Double netPremium, Double policyRights, Double policySurcharges,
                Double netPremiumTaxes, Double policyRightsTaxes, Double discountValue,
                Double firstPayment, Double subsequentPayment) {
        }
    }
}
