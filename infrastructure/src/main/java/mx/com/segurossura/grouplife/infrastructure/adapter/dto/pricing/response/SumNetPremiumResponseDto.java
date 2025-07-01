package mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response;

import java.math.BigDecimal;

public record SumNetPremiumResponseDto(String kinship, BigDecimal netPremium) {
}
