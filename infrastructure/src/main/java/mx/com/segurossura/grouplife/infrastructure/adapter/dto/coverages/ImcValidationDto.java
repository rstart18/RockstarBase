package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import java.util.List;

public record ImcValidationDto(ImcDto imc, List<AgesDto> ages) {
}
