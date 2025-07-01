package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

@Builder
public record InsuredDto(Integer min, Integer max, DiffAdminOpDto diffAdminOp) {
}
