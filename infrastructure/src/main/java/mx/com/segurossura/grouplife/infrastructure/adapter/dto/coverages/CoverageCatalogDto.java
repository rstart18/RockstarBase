package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CoverageCatalogDto(String modalityKey, String description, InsuredDto insured, AgeDto age,
                                 List<CoverageDetailDto> coverages, int maxGroups, LimitBasic limitBasic,
                                 List<SamiDto> sami, BigDecimal samiValue, List<ImcValidationDto> imcValidation, ComparatorDto actuarialAge, ComparatorDto standardDeviation,
                                 ComparatorDto quotient, Integer adjustedAverageAge) {
}
