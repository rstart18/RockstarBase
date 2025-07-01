package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog.CoveragePlanDataGtwDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission.CommissionDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {

    public static InsuranceDataDto createInsuranceDataDto() {
        final List<CatalogDataDto> catalogDataList = new ArrayList<>();
        catalogDataList.add(createCatalogDataDto());

        return new InsuranceDataDto(catalogDataList);
    }

    public static InsuranceDataDto createInsuranceDataDtoVolunteer() {
        final List<CatalogDataDto> catalogDataList = new ArrayList<>();
        catalogDataList.add(createCatalogDataDtoVolunteer());

        return new InsuranceDataDto(catalogDataList);
    }

    public static CatalogDataDto createCatalogDataDtoVolunteer() {

        final List<CoverageCatalogDto> coverageCatalogList = new ArrayList<>();
        coverageCatalogList.add(createCoverageCatalogDtoToVolunteer());

        return new CatalogDataDto("Product Description", "Product", coverageCatalogList);
    }


    public static CatalogDataDto createCatalogDataDto() {

        final List<CoverageCatalogDto> coverageCatalogList = new ArrayList<>();
        coverageCatalogList.add(createCoverageCatalogDtoToTraditional());

        return new CatalogDataDto("Product Description", "Product", coverageCatalogList);
    }

    public static CoverageDetailDto coverageDetailDto() {
        return new CoverageDetailDto("FALLECIMIENTO", "00001", "Fallecimiento",
                "BASICA", null, true, List.of(insuredValidationDto()),
                new DisplayDto("TYPE", "COLOR"),
                new GroupedDto("TITLE", "TEXT", List.of()), false);

    }

    public static YearLimitDto min() {
        return new YearLimitDto(15, "YEAR");

    }

    public static YearLimitDto minCancellation() {
        return new YearLimitDto(80, "YEAR");

    }

    public static YearLimitDto minRenovation() {
        return new YearLimitDto(79, "YEAR");

    }

    public static YearLimitDto max() {
        return new YearLimitDto(69, "YEAR");

    }

    public static DependenciesDto dependenciesDto() {
        return new DependenciesDto("coverageKey", "kinshipkey");

    }

    public static SumDto sumDto() {
        return new SumDto(new BigDecimal(500000), null, "formula", "formula");

    }

    public static InsuredSumLimitDto insuredSumLimitDto() {
        return new InsuredSumLimitDto(sumDto(), sumDto());

    }

    public static AgeLimitDto ageLimitDtoAcceptable() {
        return new AgeLimitDto(min(), max());

    }

    public static AgeLimitDto ageLimitDtoRenovation() {
        return new AgeLimitDto(minRenovation(), minRenovation());

    }

    public static AgeLimitDto ageLimitDtoCancellation() {
        return new AgeLimitDto(minCancellation(), minCancellation());

    }


    public static InsuredValidationDto insuredValidationDto() {
        return new InsuredValidationDto(null, null, null, ageLimitDtoAcceptable(), ageLimitDtoRenovation()
                , ageLimitDtoCancellation(), List.of(insuredSumLimitDto()));

    }

    public static DiffAdminOpDto diffAdminOpDto() {
        return new DiffAdminOpDto(30, "comparator");

    }

    public static InsuredDto insuredDto() {
        return new InsuredDto(7, 1000, diffAdminOpDto());

    }

    public static AgeDto ageDto() {
        return new AgeDto(12, 65, 24, 60, 41);

    }

    public static CoverageCatalogDto createCoverageCatalogDtoToTraditional() {
        return new CoverageCatalogDto("TRADICIONAL", "Coverage Description", insuredDto(), ageDto(),
                List.of(coverageDetailDto()), 10, null,null, null,null, null, null, null, null
        );
    }

    public static CoverageCatalogDto createCoverageCatalogDtoToVolunteer() {
        return new CoverageCatalogDto("VOLUNTARIA", "Coverage Description", insuredDto(), ageDto(),
                List.of(coverageDetailDto()), 3, null,null, null,null, null, null, null, null
        );
    }


    public static CoveragePlanDataGtwDto createCoveragePlanDataGtwDto() {
        return new CoveragePlanDataGtwDto(List.of(createCoveragePlanGtwDto()));
    }

    public static CoveragePlanDataGtwDto.CoveragePlanGtwDto createCoveragePlanGtwDto() {
        return new CoveragePlanDataGtwDto.CoveragePlanGtwDto(List.of(createCoveragePlanDto()));
    }

    public static CoveragePlanDataGtwDto.CoveragePlanGtwDto.CoveragePlanDto createCoveragePlanDto() {
        return new CoveragePlanDataGtwDto.CoveragePlanGtwDto.CoveragePlanDto(Modality.TRADITIONAL,
                List.of(createPlanDto()));
    }

    public static CoveragePlanDataGtwDto.CoveragePlanGtwDto.CoveragePlanDto.PlanDto createPlanDto() {
        return new CoveragePlanDataGtwDto.CoveragePlanGtwDto.CoveragePlanDto.PlanDto(
                "ESTANDAR", "estandar",
                List.of(createCoveragePlanItemDtoDto()), true, "vignette"
        );
    }

    public static CoveragePlanDataGtwDto.CoveragePlanGtwDto.CoveragePlanDto.PlanDto.CoveragePlanItemDtoDto createCoveragePlanItemDtoDto() {
        return new CoveragePlanDataGtwDto.CoveragePlanGtwDto.CoveragePlanDto.PlanDto.CoveragePlanItemDtoDto(
                "00001", "DESCRIPTION", List.of("00001")
        );
    }

    public static CommissionDataResponseDto createCommissionDataResponseDto() {

        final CommissionDataResponseDto.CommissionResponseDto commissionResponseDto = new CommissionDataResponseDto.CommissionResponseDto(
                902,
                "05470",
                "00001",
                LocalDate.now(),
                "000001",
                "C",
                0.2D,
                0.01D
        );
        return new CommissionDataResponseDto(commissionResponseDto);
    }
}
