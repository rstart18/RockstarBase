package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PaymentOptionRequestDto(
        String coverage,
        String annual,
        @JsonProperty("semi-annually") String semiAnnually,
        String quarterly,
        String monthly
) {
}
