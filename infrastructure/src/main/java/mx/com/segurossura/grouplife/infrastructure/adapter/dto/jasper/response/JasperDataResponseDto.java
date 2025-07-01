package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.response;

import lombok.Builder;

public record JasperDataResponseDto(JasperContentResponseDto data) {
    @Builder
    public record JasperContentResponseDto(
            String fileName,
            String fileText
    ) {
    }
}
