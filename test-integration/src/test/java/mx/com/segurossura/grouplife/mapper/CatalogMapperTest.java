package mx.com.segurossura.grouplife.mapper;

import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageCatalogDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageDetailDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DependenciesDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DiffAdminOpDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredSumLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredValidationDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.SumDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.YearLimitDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogMapperTest {

    private final CatalogMapper mapper = Mappers.getMapper(CatalogMapper.class);

    @Test
    void test_shouldMapCoverageCatalogDtoToModel() {
        // Given: Preparamos un CoverageCatalogDto de ejemplo
        GroupVg.builder()
                .name("fghgh")
                .groupType("SA_FIJA")
                .numAdministrativeInsured(12)
                .numOperationalInsured(12)
                .administrativeInsuredSum(new BigDecimal(12))
                .operationalInsuredSum(new BigDecimal(12))
                .salaryMonth(12)
                .averageMonthlySalary(12D)
                .coverages(List.of(CoverageDetail.builder().build()))
                .build();

        CoverageCatalogDto coverageCatalogDto = CoverageCatalogDto.builder()
                .age(
                        AgeDto.builder()
                                .min(0)
                                .max(70)
                                .averageMax(50)
                                .averageMin(23)
                                .build()
                )
                .description("Coverage")
                .modalityKey("TRADICIONAL")
                .insured(InsuredDto.builder()
                        .min(0)
                        .max(60)
                        .diffAdminOp(DiffAdminOpDto.builder()
                                .comparator(">")
                                .percentage(20)
                                .build())
                        .build())
                .coverages(List.of(
                        CoverageDetailDto.builder()
                                .coverageKey("FALLECIMIENTO")
                                .typeCoverage("BAS")
                                .code("001")
                                .description("Fallecimiento")
                                .mandatory(true)
                                .insuredValidations(List.of(
                                                InsuredValidationDto.builder()
                                                        .kinship("Titular")
                                                        .kinshipKey("T")
                                                        .acceptableYearOldLimit(
                                                                AgeLimitDto.builder()
                                                                        .min(YearLimitDto.builder()
                                                                                .unit("YEAR")
                                                                                .value(0)
                                                                                .build())
                                                                        .max(YearLimitDto.builder()
                                                                                .unit("YEAR")
                                                                                .value(100)
                                                                                .build())
                                                                        .build()
                                                        )
                                                        .cancellationYearOldLimit(null)
                                                        .renovationYearOldLimit(null)
                                                        .insuredSumLimit(List.of(
                                                                InsuredSumLimitDto.builder()
                                                                        .min(SumDto.builder()
                                                                                .defaultValue(new BigDecimal(50000))
                                                                                .formula(null)
                                                                                .formulaDescription("mayor")
                                                                                .dependencies(
                                                                                        DependenciesDto.builder()
                                                                                                .coverageKey("FALLECIMIENTO")
                                                                                                .kinshipKey("H")
                                                                                                .build()
                                                                                )
                                                                                .build())
                                                                        .max(null)
                                                                        .build()
                                                        ))
                                                        .build()
                                        )
                                )
                                .build()
                ))
                .maxGroups(10)
                .build();

        // When: Realizamos el mapeo con el mapper
        List<CoverageCatalog> coverageCatalogList = mapper.toModelList(List.of(coverageCatalogDto));

        // Then: Verificamos que los campos se mapearon correctamente
        assertEquals(coverageCatalogDto.modalityKey(), coverageCatalogList.getFirst().modalityKey());
        assertEquals(coverageCatalogDto.description(), coverageCatalogList.getFirst().description());

        String dtoString = coverageCatalogDto.toString();
        assertTrue(dtoString.contains("modalityKey=TRADICIONAL"));
        assertTrue(dtoString.contains("description=Coverage"));
    }
}
