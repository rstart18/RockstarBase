package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CoveragesDto(String code, int capitaCode, BigDecimal administrativeInsuredSum, BigDecimal operationalInsuredSum) {
}
