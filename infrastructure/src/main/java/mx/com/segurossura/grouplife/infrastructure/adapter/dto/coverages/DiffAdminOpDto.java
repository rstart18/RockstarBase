package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

@Builder
public record DiffAdminOpDto(Integer percentage, String comparator) {
}
