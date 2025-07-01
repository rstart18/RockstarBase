package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PolicyHolderRequestDto(String birthdate, BigDecimal monthlySalary, String occupation, String kinship) {
}
