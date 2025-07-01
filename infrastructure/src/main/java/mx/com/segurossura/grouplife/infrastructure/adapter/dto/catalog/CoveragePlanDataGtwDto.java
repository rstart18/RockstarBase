package mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog;

import mx.com.segurossura.grouplife.domain.model.enums.Modality;

import java.util.List;

public record CoveragePlanDataGtwDto(List<CoveragePlanGtwDto> data) {
    public record CoveragePlanGtwDto(List<CoveragePlanDto> coveragePlans) {
        public record CoveragePlanDto(Modality modalityKey, List<PlanDto> plans) {
            public record PlanDto(String planKey, String planDescription, List<CoveragePlanItemDtoDto> coverages, boolean suggestion, String vignette) {
                public record CoveragePlanItemDtoDto(String code, String description, List<String> coverages) {
                }
            }
        }
    }
}
